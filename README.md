# plainjdbc

Small Scala library inspired by Spring JdbcTemplate to execute generated sql statements over plain jdbc.
This library has no dependencies and is extremely easy to use.

```scala

case class Item(id: Int, name: String)

class ItemsRepository(val dataSource: DataSource) extends JdbcStore {

    def findById(id: Int): Try[Option[Item]] =
        query("select id, name from Items where id = ?", List(id)).map {
            case head :: _ => Some(new Item(head("id"), head("name")))
            case _ => None
        }
}
```

Released under MIT license.
