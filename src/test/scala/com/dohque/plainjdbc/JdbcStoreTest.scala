package com.dohque.plainjdbc

import java.sql.{Connection, PreparedStatement}
import javax.sql.DataSource

import org.h2.jdbcx.JdbcDataSource
import org.junit.runner.RunWith
import org.specs2.mock._
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
object JdbcStoreTest extends Specification with Mockito {

  trait WithJdbcStore extends After with JdbcStore {

    Class.forName("org.h2.Driver")

    val dataSource = new JdbcDataSource()

    dataSource.setUrl("jdbc:h2:mem:test;MODE=MSSQLServer;DATABASE_TO_UPPER=false;")
    dataSource.setUser("sa")
    dataSource.setPassword("sa")

    val connection = dataSource.getConnection

    def after = {
      connection.close
    }
  }

  "Jdbc store" should {

    "should manage connection" in {
      val dataSourceMock = mock[DataSource]
      val connection = mock[Connection]
      dataSourceMock.getConnection returns connection
      val jdbcStore = new JdbcStore {
        override def dataSource: DataSource = dataSourceMock
      }
      val result = jdbcStore.withConnection { implicit connection =>
        connection
      }
      there was one(connection).close
      result.get must be(connection)
    }

    "should manage prepared statement" in {
      implicit val connection = mock[Connection]
      val preparedStatement = mock[PreparedStatement]
      connection.prepareStatement("select * from Orders") returns preparedStatement
      val jdbcStore = new JdbcStore {
        override def dataSource: DataSource = ???
      }
      val result = jdbcStore.withPreparedStatement("select * from Orders") { preparedStatement =>
        preparedStatement
      }
      there was one(preparedStatement).close
      result.get must be(preparedStatement)
    }

    "should set parameters on prepared statement" in {
      val preparedStatement = mock[PreparedStatement]
      val jdbcStore = new JdbcStore {
        override def dataSource: DataSource = ???
      }
      jdbcStore.setParams(preparedStatement, List(2))
      there was one(preparedStatement).setObject(1, 2)
    }

    "should query updated data" in new WithJdbcStore {
      update(
        """
          | create table Orders (
          |   id serial not null primary key,
          |   item_id integer
          | )
        """.stripMargin
      )
      update("insert into Orders (id, item_id) values (1, 42)")
      val result = queryMap("select * from Orders where id = ?", List(1))
      result.get must be equalTo(List(Map("id" -> 1, "item_id" -> 42)))
    }
  }
}
