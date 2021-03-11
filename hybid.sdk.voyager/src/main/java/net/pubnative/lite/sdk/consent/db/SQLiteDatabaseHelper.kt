package net.pubnative.lite.sdk.consent.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmErasure

/**
 *
 * a helper class for Sqlite Database management, it contain function for create table,
 * insert, update, delete, count the data in a easy way
 *
 * You create a subclass of [SQLiteDatabaseHelper] implementing
 * [onCreate] for init the database file
 * [onUpgrade] for Database versioning and migration data
 * [onUpgrade] will call when the
 * @param[version] is higher than the current database file version
 *
 */
abstract class SQLiteDatabaseHelper(context: Context,
                                    name: String?,
                                    factory: SQLiteDatabase.CursorFactory?,
                                    version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        val SPACE = " "

        val CREATE_TABLE = "CREATE TABLE"
        val IF_NOT_EXIST = "IF NOT EXISTS"
        val IF_EXIST = "IF EXISTS"
        val NOT_NULL = "not null"
        val PRIMARY_KEY = "primary key"
        val AUTO_INCREMENT = "autoincrement"
        val UNIQUE = "unique"

        val WHERE = "WHERE"
        val ORDER_BY = "ORDER BY"
        val COUNT_SQL_QUERY = "SELECT 1 FROM "
    }

    /**
     * Generate Sqlite table using Kotlin class with [Database] annotation
     *
     * @param [tableClass] Kotlin class with [Database] annotation
     */
    fun createTable(tableClass: KClass<*>) {
        val (tableName, fieldMap) = validateValidClass(tableClass)

        val sb = StringBuilder()
        sb.append(CREATE_TABLE)
        sb.append(SPACE)
        sb.append(IF_NOT_EXIST)
        sb.append(SPACE)
        sb.append(tableName)
        sb.append("(")
        fieldMap.keys.forEachIndexed { index, key ->
            val obj = fieldMap[key]
            if (obj != null && obj.returnType.jvmErasure.getDataBaseFieldType().isNotEmpty()) {
                sb.append(key)
                sb.append(SPACE)
                sb.append(obj.returnType.jvmErasure.getDataBaseFieldType())
                sb.append(SPACE)
                if (obj.isDataBaseFieldGeneratedId() ?: false) {
                    sb.append(PRIMARY_KEY)
                    sb.append(SPACE)
                }
                if (obj.isDataBaseFieldAutoIncrease() ?: false) {
                    sb.append(AUTO_INCREMENT)
                    sb.append(SPACE)
                }
                if (obj.isDataBaseFieldNonNullable() ?: false) {
                    sb.append(NOT_NULL)
                    sb.append(SPACE)
                }
                if (obj.isDataBaseFieldUnique() ?: false) {
                    sb.append(UNIQUE)
                    sb.append(SPACE)
                }
                if (index != fieldMap.keys.size - 1) {
                    sb.append(",")
                }
            }
        }
        sb.append(");")
        writableDatabase.execSQL(sb.toString())
    }

    /**
     * Insert Row into Database
     *
     * @param [obj] The instance of Kotlin Class with [Database] and [Schema] annotation, it can be Collection of target Kotlin class
     */
    fun insert(obj: Any): Long {
        when (obj) {
            is Collection<*> -> {
                for (single in obj) {
                    return insertObj(single)
                }
            }
            else -> {
                return insertObj(obj)
            }
        }

        return -1
    }

    /**
     * Internal function to insert single object
     */
    internal fun insertObj(obj: Any?): Long {
        if (obj == null) {
            return -1
        }
        val (tableName, fieldMap) = validateValidClass(obj::class)

        val contentValues = ContentValues()
        for (key in fieldMap.keys) {
            if ((fieldMap[key]?.javaField?.annotations?.find { it is Schema } as? Schema)?.generatedId == true) {
                continue
            }
            val value = obj.getDataBaseFieldValue(key = key)
            if (value != null) {
                contentValues.put(key, value)
            }
        }
        return writableDatabase.insert(tableName, null, contentValues)
    }

    /**
     * Update Row into Database
     *
     * @param [obj] The instance of Kotlin Class with [Database] and [Schema] annotation, it can be Collection of target Kotlin class
     */
    fun update(obj: Any) {
        when (obj) {
            is Collection<*> -> {
                for (single in obj) {
                    updateObject(single)
                }
            }
            else -> {
                updateObject(obj)
            }
        }
    }

    /**
     * Internal function to update single object
     */
    internal fun updateObject(obj: Any?) {
        if (obj == null) {
            return
        }
        val (tableName, fieldMap) = validateValidClass(obj::class)
        var whereClause: String? = null
        var args: Array<String>? = null

        val contentValues = ContentValues()
        for (key in fieldMap.keys) {
            val schema: Schema? = fieldMap[key]?.javaField?.annotations?.find { it is Schema } as? Schema
            if (schema?.generatedId ?: false) {
                val field = schema?.field
                if (field != null && obj.getDataBaseFieldValue(key = key) != null) {
                    whereClause = schema.field + Where.IStatement.Equal
                    args = arrayOf(obj.getDataBaseFieldValue(key = key).toString())
                }
                continue
            }
            val value = obj.getDataBaseFieldValue(key = key)
            if (value != null) {
                contentValues.put(key, value)
            }
        }
        writableDatabase.update(tableName, contentValues, whereClause, args)
    }

    /**
     * Update Multiple Row into Database matching the given [where], or 'null' to update all row in table
     *
     * @param [obj] The instance of Kotlin Class with [Database] and [Schema] annotation, it can not be Collections
     *
     */
    fun update(obj: Any, where: (Where.() -> Where)? = null) {
        if (obj::class.isSubclassOf(Collection::class)) {
            return
        }
        val (tableName, fieldMap) = validateValidClass(obj::class)

        val (whereClause, args) = getWhereStatement(where)

        val contentValues = ContentValues()
        for (key in fieldMap.keys) {
            val value = obj.getDataBaseFieldValue(key = key)
            if (value != null) {
                contentValues.put(key, value)
            }
        }
        writableDatabase.update(tableName, contentValues, whereClause, args)
    }

    /**
     * Update Multiple Row with specific column into Database matching the given [where], or 'null' to update all row in table
     *
     * @param [updateFieldMap] key-value pair of target update field in data base with [HashMap] expression
     * @param [kClass] the target Kotlin Class with [Database] and [Schema] annotation
     *
     */
    fun <T : Any> update(updateFieldMap: HashMap<String, Any>, kClass: KClass<T>, where: (Where.() -> Where)? = null) {
        val (tableName) = validateValidClass(kClass)
        val (whereClause, args) = getWhereStatement(where)
        val contentValues = ContentValues()
        for (key in updateFieldMap.keys) {
            val value = updateFieldMap[key]
            if (value != null) {
                contentValues.put(key, value)
            }
        }
        writableDatabase.update(tableName, contentValues, whereClause, args)
    }

    /**
     * Delete Multiple Row with specific column into Database matching the given [where], or 'null' to delete all row in table
     *
     * @param [kClass] the target Kotlin Class with [Database] and [Schema] annotation
     *
     */
    fun <T : Any> delete(kClass: KClass<T>, where: (Where.() -> Where)? = null) {
        val (tableName) = validateValidClass(kClass::class)
        val (whereClause, args) = getWhereStatement(where)
        writableDatabase.delete(tableName, whereClause, args)
    }

    /**
     * Delete Row into Database
     *
     * @param [obj] The instance of Kotlin Class with [Database] and [Schema] annotation, it can be Collection of target Kotlin class
     */
    fun delete(obj: Any) {
        when (obj) {
            is Collection<*> -> {
                for (single in obj) {
                    deleteObject(single)
                }
            }
            else -> {
                deleteObject(obj)
            }
        }
    }

    /**
     * Internal function to delete single object
     */
    internal fun deleteObject(obj: Any?) {
        if (obj == null) {
            return
        }
        val (tableName, fieldMap) = validateValidClass(obj::class)
        var whereClause: String? = null
        var args: Array<String>? = null

        for (key in fieldMap.keys) {
            val schema: Schema? = fieldMap[key]?.javaField?.annotations?.find { it is Schema } as? Schema
            if (schema?.generatedId == true) {
                val field = schema.field
                if (field != null && obj.getDataBaseFieldValue(key = key) != null) {
                    whereClause = schema.field + Where.IStatement.Equal
                    args = arrayOf(obj.getDataBaseFieldValue(key = key).toString())
                }
                break
            }
        }
        writableDatabase.delete(tableName, whereClause, args)
    }

    /**
     * Read Multiple Row in Table matching the given [where], or 'null' to read all row in table
     *
     * @param [kClass] the target Kotlin Class with [Database] and [Schema] annotation
     * @return [List] The List of result with type [T]
     */
    fun <T : Any> get(kClass: KClass<T>, where: (Where.() -> Where)? = null): List<T>? {
        val (tableName, fieldMap) = validateValidClass(kClass)
        val rDB = readableDatabase
        val outputArr = Array(fieldMap.keys.size, { i ->
            fieldMap.keys.elementAt(i)
        })

        val (whereClause, args, order) = getWhereStatement(where)

        val c = rDB.query(tableName, outputArr, whereClause, args, null, null, order)
        val ret = getCursorObjects(kClass, c)
        c.close()
        return ret
    }

    /**
     * Count number of entries in Table matching the given [where], or 'null' to read all row in table
     *
     * @param [tableClass] the target Kotlin Class with [Database] annotation
     * @return [Int] The Number of entries
     */
    fun count(tableClass: KClass<*>, where: (Where.() -> Where)? = null): Int {
        val (tableName) = validateValidClass(tableClass)
        val rDB = readableDatabase
        val (whereClause, args) = getWhereStatement(where)
        val sqlQuery = StringBuilder(COUNT_SQL_QUERY)
        sqlQuery.append(tableName)
        if (!whereClause.isNullOrEmpty()) {
            sqlQuery.append(SPACE)
            sqlQuery.append(WHERE)
            sqlQuery.append(SPACE)
            sqlQuery.append(whereClause)
        }
        val c = rDB.rawQuery(sqlQuery.toString(), args)
        val count = c.count
        c.close()
        return count
    }

    /**
     * Run the Raw sql statement, it can return [List] of [T] using 'SELECT' statement when [objClass] is not 'null'
     *
     * @param [objClass] The target Kotlin Class with [Database] annotation, ignore it if statement is not 'SELECT'
     * @param [sqlString] The sql statement which will execute
     */
    fun <T : Any> execRawSQL(objClass: KClass<T>? = null, sqlString: String): List<T>? {
        if (objClass == null) {
            writableDatabase.rawQuery(sqlString, null)
            return null
        } else {
            val (tableName, fieldMap) = validateValidClass(objClass)
            val c = readableDatabase.rawQuery(sqlString, null)
            val ret = getCursorObjects(objClass, c)
            c.close()
            return ret
        }
    }

    /**
     * Close the Database after using
     */
    fun closeDatabase() {
        if (writableDatabase.isOpen) {
            writableDatabase.close()
        }
        if (readableDatabase.isOpen) {
            readableDatabase.close()
        }
    }

    /**
     * Private function to convert all database data to Kotlin Object type [T]
     *
     * @param [objClass] The Kotlin Class of [T]
     * @param [c] The [Cursor] instance after execute sql statement
     *
     * @return [List] The List of type [T] after convert
     */
    private fun <T : Any> getCursorObjects(objClass: KClass<T>, c: Cursor): List<T> {
        val ret = mutableListOf<T>()
        while (c.moveToNext()) {
            val constructor = objClass.primaryConstructor
            val paramsMap = hashMapOf<KParameter, Any?>()
            if (constructor != null) {
                val properties = objClass.memberProperties
                for (property in properties) {
                    val fieldName = (property.javaField?.annotations?.find { it is Schema } as? Schema)?.field
                    if (fieldName?.isNotEmpty() ?: false) {
                        val data = when (property.returnType.jvmErasure) {
                            String::class -> c.getString(c.getColumnIndex(fieldName))
                            Date::class -> Date(c.getLong(c.getColumnIndex(fieldName)))
                            Boolean::class -> c.getInt(c.getColumnIndex(fieldName)) == 1
                            Char::class -> c.getString(c.getColumnIndex(fieldName))
                            Byte::class -> c.getInt(c.getColumnIndex(fieldName))
                            Short::class -> c.getInt(c.getColumnIndex(fieldName))
                            Int::class -> c.getInt(c.getColumnIndex(fieldName))
                            Long::class -> c.getLong(c.getColumnIndex(fieldName))
                            Float::class -> c.getFloat(c.getColumnIndex(fieldName))
                            Double::class -> c.getDouble(c.getColumnIndex(fieldName))
                            ByteArray::class -> c.getBlob(c.getColumnIndex(fieldName))
                            BigDecimal::class -> BigDecimal(c.getDouble(c.getColumnIndex(fieldName)))
                            else -> null
                        }
                        val kParams = constructor.findParameterByName(property.name)
                        if (kParams != null) {
                            paramsMap.put(kParams, data)
                        }
                    }
                }
                ret.add(constructor.callBy(paramsMap))
            }
        }
        return ret
    }

    /**
     * Private function to validate if the Kotlin Class contain table name annotation [Database] and Schema annotation [Schema]
     *
     * @throws [IllegalArgumentException] will throw if the Kotlin Class does not match the limitation
     */
    private fun validateValidClass(kClass: KClass<*>): DataBaseSchema {
        if (!kClass.isData) {
            throw IllegalArgumentException("Object Must Be Instance of Data Class")
        }
        val tableName = kClass.getTableName()
        if (tableName.isEmpty()) {
            throw IllegalArgumentException("Object Must Be Contain Table Name")
        }
        val fieldMap = kClass.getDataBaseField()
        if (fieldMap.isEmpty()) {
            throw IllegalArgumentException("Object Must Be Contain Field(s)")
        }
        return DataBaseSchema(tableName, fieldMap)
    }

    /**
     * Private function to get where statement clause and argument
     *
     * @param [where] The lambdas of [Where] to apply
     *
     * @return [Statements]
     */
    private fun getWhereStatement(where: (Where.() -> Where)? = null): Statements {
        var whereClause: String? = null
        var args: Array<String>? = null
        var order: String? = null
        if (where != null) {
            val whereObj = Where()
            whereObj.where()
            whereClause = whereObj.getClauseString()
            args = whereObj.getArgs()
            order = whereObj.getOrder()
        }
        return Statements(whereClause, args, order)
    }
}

/**
 * Private function for input different content type into [ContentValues] due to [ContentValues] need a specific Object Type
 */
private fun ContentValues.put(key: String, value: Any) {
    when (value) {
        is String -> this.put(key, value)
        is Date -> this.put(key, value.time)
        is Boolean -> this.put(key, value)
        is Char -> this.put(key, value.toString())
        is Byte -> this.put(key, value)
        is Short -> this.put(key, value)
        is Int -> this.put(key, value)
        is Long -> this.put(key, value)
        is Float -> this.put(key, value)
        is Double -> this.put(key, value)
        is ByteArray -> this.put(key, value)
        is BigDecimal -> this.put(key, value.toDouble())

    }
}

/**
 * Internal Data Class for storing Database table name, database fields for function return
 *
 * @see [SQLiteDatabaseHelper.validateValidClass]
 * @param [tableName] Table name of the Kotlin Class
 * @param [fieldMap] The Map of database field in Kotlin Class
 */
internal data class DataBaseSchema(
        val tableName: String,
        val fieldMap: HashMap<String, KProperty1<*, *>>
)