package conduit.repository

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table("users") {
    val id = integer("id").primaryKey().autoIncrement()
    val email = varchar("email", 254).uniqueIndex()
    val password = varchar("password", 255)
    val username = varchar("name", 50).uniqueIndex()
    val bio = varchar("bio", 255).nullable()
    val image = varchar("image", 255).nullable()
}

object Following : Table("following") {
    val sourceId = integer("source_id").primaryKey() references Users.id
    val targetId = integer("target_id").primaryKey() references Users.id
}

object Articles : IntIdTable("articles") {
    val slug = varchar("slug", 255).uniqueIndex()
    val title = varchar("title", 255)
    val description = varchar("description", 255)
    val body = text("body")
    val createdAt = datetime("createdAt")
    val updatedAt = datetime("updatedAt")
    val authorId = integer("author_id") references Users.id
}

object Tags : Table("tags") {
    val articleId = reference("article_id", Articles, ReferenceOption.CASCADE)
    val tag = varchar("tag", 255)
}

object Favorites : Table("favorites") {
    val userId = integer("user_id") references Users.id
    val articleId = reference("article_id", Articles)
}

object Comments : Table("comments") {
    val id = integer("id").primaryKey().autoIncrement()
    val createdAt = datetime("createdAt")
    val updatedAt = datetime("updatedAt")
    val body = text("body")
    val authorId = integer("author_id") references Users.id
    val articleId =  reference("article_id", Articles, ReferenceOption.CASCADE)
}

fun createDb(url: String, driver: String): Database {
    val database = Database.connect(url, driver = driver)
    transaction(database) {
        SchemaUtils.create(Users)
        SchemaUtils.create(Following)
        SchemaUtils.create(Articles)
        SchemaUtils.create(Tags)
        SchemaUtils.create(Favorites)
        SchemaUtils.create(Comments)
    }
    return database
}