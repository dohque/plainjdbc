package com.dohque.plainjdbc

import java.sql.{Connection, PreparedStatement}
import javax.sql.DataSource

import scala.util.Try

trait JdbcStore extends HasDataSource {

  def update(sql: String, params: Any*): Try[Int] =
    withConnection { implicit connection =>
      withPreparedStatement(sql) { preparedStatement =>
        setParams(preparedStatement, params)
        preparedStatement.executeUpdate
      }
    }.flatten


  def query(sql: String, params: Seq[Any]): Try[List[Map[String, Any]]] =
    withConnection { implicit connection =>
      withPreparedStatement(sql) { preparedStatement =>
        setParams(preparedStatement, params)
        val resultSet = preparedStatement.executeQuery
        try {
          Iterator.continually((resultSet.next(), resultSet))
            .takeWhile(_._1).map {
            case (_, resultSet) =>
              val metaData = resultSet.getMetaData
              (
                for (i <- 1 to metaData.getColumnCount)
                  yield metaData.getColumnName(i) -> resultSet.getObject(i)
                ).toMap
          }.toList
        } finally {
          resultSet.close
        }
      }
    }.flatten

  def withConnection[T](inConnection: Connection => T): Try[T] = Try {
    val connection = dataSource.getConnection
    try {
      inConnection(connection)
    } finally {
      connection.close()
    }
  }

  def withPreparedStatement[T](sql: String)(inPreparedStatement: PreparedStatement => T)
                              (implicit connection: Connection): Try[T] = Try {
    val preparedStatement = connection.prepareStatement(sql)
    try {
      inPreparedStatement(preparedStatement)
    } finally {
      preparedStatement.close()
    }
  }

  def setParams(preparedStatement: PreparedStatement, params: Seq[Any]): Unit = {
    for (i <- 1 to params.length) {
      preparedStatement.setObject(i, params(i - 1))
    }
  }
}


trait HasDataSource {

  def dataSource: DataSource
}
