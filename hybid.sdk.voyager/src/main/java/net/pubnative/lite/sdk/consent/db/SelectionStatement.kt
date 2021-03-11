package net.pubnative.lite.sdk.consent.db

import java.util.*


interface ISelectionOperator<WHERE: ISelectionOperator<WHERE, OPERATOR>,
        OPERATOR: ISelectionOperator<WHERE, OPERATOR>> {

    /**
     *
     * This function is for equal statement
     * @param key   Column Name in the table
     *  @param value   the target row value
     */
    fun eq(key: String, value: Any): WHERE
    /**
     *
     * This function is for Not equal statement
     * @param key   Column Name in the table
     *  @param value   the target row value
     */
    fun notEq(key: String, value: Any): WHERE
    /**
     *
     * This function is for Greater Than statement
     * @param key   Column Name in the table
     *  @param value   the target row value in Number
     */
    fun greaterThan(key: String, value: Number): WHERE
    /**
     *
     * This function is for Greater Than Or Equal statement
     * @param key   Column Name in the table
     *  @param value   the target row value in Number
     */
    fun greaterThanOrEq(key: String, value: Number): WHERE
    /**
     *
     * This function is for Smaller Than statement
     * @param key   Column Name in the table
     *  @param value   the target row value in Number
     */
    fun smallerThan(key: String, value: Number): WHERE
    /**
     *
     * This function is for Smaller Than Or Equal statement
     * @param key   Column Name in the table
     *  @param value   the target row value in Number
     */
    fun smallerThanOrEq(key: String, value: Number): WHERE
    /**
     *
     * This function is for Between statement
     * @param key   Column Name in the table
     * @param first   the target row value in Number
     * @param second   the target row value in Number
     */
    fun between(key: String, first: Number, second: Number): WHERE
    /**
     *
     * This function is for Between statement
     * @param key   Column Name in the table
     * @param first   the target row value in Date
     * @param second   the target row value in Date
     */
    fun between(key: String, first: Date, second: Date): WHERE
    /**
     *
     * This function is for LIKE statement
     * @param key   Column Name in the table
     *  @param value   the target row value in String
     */
    fun containString(key: String, value: String): WHERE
    /**
     * This function is for link up different statement with AND
     */
    fun and(): OPERATOR
    /**
     * This function is for link up different statement with OR
     */
    fun or(): OPERATOR

    enum class Order {
        Ascending,
        Descending;

        fun getClauseString(): String {
            return when (this) {
                Ascending -> "ASC"
                Descending -> "DESC"
            }
        }
    }
}

class Where: ISelectionOperator<Where, Where.Operator> {
    companion object {
        val SPACE = " "
    }
    private val mStatements = ArrayList<IStatement>()
    private var mOrder: IStatement? = null

    override fun eq(key: String, value: Any): Where {
        mStatements.add(EqStatement(key, value))
        return this
    }

    override fun notEq(key: String, value: Any): Where {
        mStatements.add(NotEqStatement(key, value))
        return this
    }

    override fun greaterThan(key: String, value: Number): Where {
        mStatements.add(GreaterThanStatement(key, value))
        return this
    }

    override fun greaterThanOrEq(key: String, value: Number): Where {
        mStatements.add(GreaterThanStatement(key, value, true))
        return this
    }

    override fun smallerThan(key: String, value: Number): Where {
        mStatements.add(SmallerThanStatement(key, value))
        return this
    }

    override fun smallerThanOrEq(key: String, value: Number): Where {
        mStatements.add(SmallerThanStatement(key, value, true))
        return this
    }

    override fun between(key: String, first: Number, second: Number): Where {
        mStatements.add(BetweenStatement(key, first, second))
        return this
    }

    override fun between(key: String, first: Date, second: Date): Where {
        mStatements.add(BetweenStatement(key, first, second))
        return this
    }

    override fun containString(key: String, value: String): Where {
        mStatements.add(LikeStatement(key, value))
        return this
    }

    private fun addOperatorFunction(operatorFunction: IStatement): Where {
        if (mStatements.isEmpty()) {
            throw IllegalArgumentException("And statement cannot be the first params")
        }
        mStatements.add(operatorFunction)
        return this
    }

    /**
     * This function is add order by statement in selection cause
     *
     * @param [order] The orderBy type , Ascending/ Descending
     * @see[ISelectionOperator.Order]
     */
    fun orderBy(order: ISelectionOperator.Order, vararg key: String): Where {
        this.mOrder = OrderStatement(key.toList(), order)
        return this
    }

    override fun and(): Operator {
        return Operator(this).and()
    }

    override fun or(): Operator {
        return Operator(this).or()
    }

    /**
     * Internal function to get Selection Clause String for Sqlite statement
     */
    internal fun getClauseString(): String? {
        val sb = StringBuilder()
        mStatements.forEach {
            sb.append(it.getStatementString())
            sb.append(SPACE)
        }
        if (sb.isNotEmpty()) {
            return sb.toString()
        } else {
            return null
        }
    }

    /**
     * Internal function to get Clause arguments for Sqlite statement
     */
    internal fun getArgs(): Array<String>? {
        val stringArr = ArrayList<String>()
        mStatements.forEach {
            val arr = it.getArgs()
            if (arr != null) {
                stringArr.addAll(arr)
            }
        }
        if (stringArr.isNotEmpty()) {
            return stringArr.toArray(arrayOfNulls(stringArr.size))
        } else {
            return null
        }
    }

    /**
     * Internal function to get Order By Type
     */
    internal fun getOrder(): String? {
        return mOrder?.getStatementString()
    }

    class Operator internal constructor(val where: Where): ISelectionOperator<Where, Operator> {
        override fun eq(key: String, value: Any): Where {
            where.eq(key, value)
            return where
        }

        override fun notEq(key: String, value: Any): Where {
            where.notEq(key, value)
            return where
        }

        override fun greaterThan(key: String, value: Number): Where {
            where.greaterThan(key, value)
            return where
        }

        override fun greaterThanOrEq(key: String, value: Number): Where {
            where.greaterThanOrEq(key, value)
            return where
        }

        override fun smallerThan(key: String, value: Number): Where {
            where.smallerThan(key, value)
            return where
        }

        override fun smallerThanOrEq(key: String, value: Number): Where {
            where.smallerThanOrEq(key, value)
            return where
        }

        override fun between(key: String, first: Number, second: Number): Where {
            where.between(key, first, second)
            return where
        }

        override fun between(key: String, first: Date, second: Date): Where {
            where.between(key, first, second)
            return where
        }

        override fun containString(key: String, value: String): Where {
            where.containString(key, value)
            return where
        }

        override fun and(): Operator {
            where.addOperatorFunction(Condition.And)
            return this
        }

        override fun or(): Operator {
            where.addOperatorFunction(Condition.Or)
            return this
        }
    }

    private data class EqStatement(val key: String,
                                   val value: Any): IStatement {
        override fun getStatementString(): String {
            return key + IStatement.Equal
        }

        override fun getArgs(): Array<String> {
            return arrayOf(value.toString())
        }
    }

    private data class NotEqStatement(val key: String,
                                      val value: Any): IStatement {
        override fun getStatementString(): String {
            return key + IStatement.NotEqual
        }

        override fun getArgs(): Array<String> {
            return arrayOf(value.toString())
        }
    }

    private data class GreaterThanStatement(val key: String,
                                            val value: Number,
                                            val isEqual: Boolean = false): IStatement {
        override fun getStatementString(): String {
            return key + when(isEqual){
                true -> IStatement.GreaterThanOrEq
                false -> IStatement.GreaterThan
            }
        }

        override fun getArgs(): Array<String> {
            return arrayOf(value.toString())
        }
    }

    private data class SmallerThanStatement(val key: String,
                                            val value: Number,
                                            val isEqual: Boolean = false): IStatement {
        override fun getStatementString(): String {
            return key + when(isEqual){
                true -> IStatement.SmallerThanOrEq
                false -> IStatement.SmallerThan
            }
        }

        override fun getArgs(): Array<String> {
            return arrayOf(value.toString())
        }
    }

    private data class BetweenStatement(val key: String,
                                        val first: Any,
                                        val second: Any): IStatement {
        override fun getStatementString(): String {
            return key + IStatement.Between
        }

        override fun getArgs(): Array<String>? {
            when (first) {
                is Date -> {
                    when (second) {
                        is Date -> return arrayOf(first.time.toString(), second.time.toString())
                        else -> return null
                    }
                }
                else -> return arrayOf(first.toString(), second.toString())
            }
        }
    }

    private data class LikeStatement(val key: String,
                                     val value: String): IStatement {
        override fun getStatementString(): String {
            return key + IStatement.Like
        }

        override fun getArgs(): Array<String> {
            return arrayOf("%$value%")
        }
    }

    private data class OrderStatement(val key: List<String>,
                                      val order: ISelectionOperator.Order): IStatement {
        override fun getStatementString(): String {
            val sb = StringBuilder()
            key.forEachIndexed {
                index, s ->
                sb.append(s)
                if (index < key.size - 1) {
                    sb.append(",")
                }
            }
            sb.append(SPACE)
            sb.append(order.getClauseString())
            return sb.toString()
        }

        override fun getArgs(): Array<String>? {
            return null
        }
    }

    enum class Condition: IStatement {
        And,
        Or,
        Between,
        Larger,
        Smaller;

        override fun getArgs(): Array<String>? {
            return null
        }

        override fun getStatementString(): String {
            return when (this) {
                And -> "AND"
                Or -> "OR"
                else -> ""
            }
        }
    }


    internal interface IStatement {
        companion object {
            val Equal = " = ?"
            val NotEqual = " != ?"
            val GreaterThan = " > ?"
            val GreaterThanOrEq = " >= ?"
            val SmallerThan = " < ?"
            val SmallerThanOrEq = " <= ?"
            val Between = " BETWEEN ? AND ?"
            val Like = " LIKE ?"
        }
        fun getStatementString(): String
        fun getArgs(): Array<String>?
    }
}

/**
 * Internal data class for storing selection statement clause and arguments
 *
 * @param [whereClause] The selection statement string for sqlite statement
 * @param [whereArgs] The argument for the selection statement
 * @param [order] The Order By Type String for sqlite statement
 */
internal data class Statements(
        val whereClause: String?,
        val whereArgs: Array<String>?,
        val order: String?
)