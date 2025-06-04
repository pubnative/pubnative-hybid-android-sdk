// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.util

import net.pubnative.lite.demo.models.Quote

object SampleQuotes {

    val list: List<Quote>

    init {
        list = getSampleQuotes()
    }

    private fun getSampleQuotes(): List<Quote> {
        val q1 = Quote(
                "Our world is built on biology and once we begin to understand it, it then becomes a technology.",
                "Ryan Bethencourt")
        val q2 = Quote(
                "Happiness is not an ideal of reason but of imagination",
                "Immanuel Kant")
        val q3 = Quote(
                "Science and technology revolutionize our lives, but memory, tradition and myth frame our response.",
                "Arthur M. Schlesinger")
        val q4 = Quote(
                "It's not a faith in technology. It's faith in people.",
                "Steve Jobs")
        val q5 = Quote(
                "We can't blame the technology when we make mistakes.",
                "Tim Berners-Lee")
        val q6 = Quote(
                "Life must be understood backward. But it must be lived forward.",
                "Søren Kierkegaard")
        val q7 = Quote(
                "Happiness can be found, even in the darkest of times, if one only remembers to turn on the light.",
                "Albus Dumbledore")
        val q8 = Quote(
                "To live a creative life, we must lose our fear of being wrong.",
                "Joseph Chilton Pearce")
        val q9 = Quote(
                "It is undesirable to believe a proposition when there is no ground whatever for supposing it true.",
                "Bertrand Russell")
        val q10 = Quote(
                "There’s always a bigger fish.",
                "Qui-Gon Jinn")
        val q11 = Quote(
                "A wizard is never late. Nor is he early. He arrives precisely when he means to.\n",
                "Gandalf")
        val q12 = Quote(
                "Moonlight drowns out all but the brightest stars.",
                "J. R. R. Tolkien, The Lord of the Rings")
        val q13 = Quote(
                "A hunted man sometimes wearies of distrust and longs for friendship.",
                "J. R. R. Tolkien, The Lord of the Rings")
        return listOf(q1, q10, q2, q3, q4, q5, q12, q6, q7, q8, q9, q11, q13)
    }
}