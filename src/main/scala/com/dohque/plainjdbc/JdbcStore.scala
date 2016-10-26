package com.dohque.plainjdbc

import java.sql.{ResultSet, Connection, PreparedStatement}
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

  def queryMap(sql: String, params: Seq[Any]): Try[List[Map[String, Any]]] =
    query[Map[String, Any]](sql, params) { resultSet: ResultSet =>
      val metaData = resultSet.getMetaData
      (for (i <- 1 to metaData.getColumnCount)
        yield metaData.getColumnName(i) -> resultSet.getObject(i)
      ).toMap
    }

  def query[T](sql: String, params: Seq[Any])(rowMapper: ResultSet => T): Try[List[T]] =
    withConnection { implicit connection =>
      withPreparedStatement(sql) { preparedStatement =>
        setParams(preparedStatement, params)
        val resultSet = preparedStatement.executeQuery
        try {
          Iterator.continually((resultSet.next(), resultSet))
            .takeWhile(_._1).map(_._2).map(rowMapper).toList
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
