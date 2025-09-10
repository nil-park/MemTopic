package com.nolbee.memtopic.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentParserTest {

    @Test
    fun `parseContentToSentences should handle empty string`() {
        val result = ContentParser.parseContentToSentences("")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseContentToSentences should handle blank string`() {
        val result = ContentParser.parseContentToSentences("   ")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseContentToSentences should handle single sentence`() {
        val result = ContentParser.parseContentToSentences("Hello world.")
        assertEquals(listOf("Hello world."), result)
    }

    @Test
    fun `parseContentToSentences should handle multiple sentences`() {
        val content = "Hello world. This is a test! How are you? Fine, thanks."
        val result = ContentParser.parseContentToSentences(content)
        assertEquals(
            listOf(
                "Hello world.",
                "This is a test!",
                "How are you?",
                "Fine, thanks."
            ),
            result
        )
    }

    @Test
    fun `parseContentToSentences should handle abbreviations correctly`() {
        val content = "Dr. Smith went to N.Y.C. yesterday."
        val result = ContentParser.parseContentToSentences(content)
        assertEquals(listOf("Dr. Smith went to N.Y.C. yesterday."), result)
    }

    @Test
    fun `parseContentToSentences should handle decimal numbers`() {
        val content = "I have 2.5 dollars. The price is 3.14 euros."
        val result = ContentParser.parseContentToSentences(content)
        assertEquals(
            listOf(
                "I have 2.5 dollars.",
                "The price is 3.14 euros."
            ),
            result
        )
    }

    @Test
    fun `parseContentToSentences should normalize multiple whitespaces`() {
        val content = "First sentence.   \n\n  Second sentence."
        val result = ContentParser.parseContentToSentences(content)
        assertEquals(
            listOf(
                "First sentence.",
                "Second sentence."
            ),
            result
        )
    }

    @Test
    fun `parseContentToSentences should handle newlines properly`() {
        val content = "First sentence.\nSecond sentence.\rThird sentence."
        val result = ContentParser.parseContentToSentences(content)
        assertEquals(
            listOf(
                "First sentence.",
                "Second sentence.",
                "Third sentence."
            ),
            result
        )
    }

    @Test
    fun `parseContentToSentences should handle Korean text`() {
        val content = "안녕하세요. 테스트입니다! 어떻게 지내세요?"
        val result = ContentParser.parseContentToSentences(content)
        assertEquals(
            listOf(
                "안녕하세요.",
                "테스트입니다!",
                "어떻게 지내세요?"
            ),
            result
        )
    }

    @Test
    fun `parseContentToSentences should handle mixed language content`() {
        val content = "Hello world. 안녕하세요! This is mixed content. 혼합 콘텐츠입니다."
        val result = ContentParser.parseContentToSentences(content)
        assertEquals(
            listOf(
                "Hello world.",
                "안녕하세요!",
                "This is mixed content.",
                "혼합 콘텐츠입니다."
            ),
            result
        )
    }

    @Test
    fun `parseContentToSentences should handle unicode punctuation`() {
        val content = "Hello world。 Unicode punctuation！ Japanese question？"
        val result = ContentParser.parseContentToSentences(content)
        assertEquals(
            listOf(
                "Hello world。",
                "Unicode punctuation！",
                "Japanese question？"
            ),
            result
        )
    }

    @Test
    fun `parseContentToSentences should handle edge case with only punctuation`() {
        val content = "... !!! ???"
        val result = ContentParser.parseContentToSentences(content)
        assertEquals(listOf("... !!! ???"), result)
    }

//    @Test
//    fun `parseContentToSentences should handle common abbreviations`() {
//        val content = "Mr. and Mrs. Smith live in U.S.A. They moved from U.K."
//        val result = ContentParser.parseContentToSentences(content)
//        assertEquals(
//            listOf(
//                "Mr. and Mrs. Smith live in U.S.A.",
//                "They moved from U.K."
//            ),
//            result
//        )
//    }

    @Test
    fun `parseContentToSentences should handle line breaks without punctuation`() {
        val content =
            "First line without punctuation\nSecond line also without punctuation\nThird line with period."
        val result = ContentParser.parseContentToSentences(content)
        assertEquals(
            listOf(
                "First line without punctuation",
                "Second line also without punctuation",
                "Third line with period."
            ),
            result
        )
    }

    @Test
    fun `parseContentToSentences should handle mixed line breaks and punctuation`() {
        val content = "Line with period.\nLine without punctuation\nAnother line!"
        val result = ContentParser.parseContentToSentences(content)
        assertEquals(
            listOf(
                "Line with period.",
                "Line without punctuation",
                "Another line!"
            ),
            result
        )
    }

    @Test
    fun `parseContentToSentences should keep quoted dialogue with tag as one sentence`() {
        val content = "\"Oh no, How will I chop wood without my axe?\" he cried."
        val result = ContentParser.parseContentToSentences(content)
        assertEquals(
            listOf("\"Oh no, How will I chop wood without my axe?\" he cried."),
            result
        )
    }

    @Test
    fun `parseContentToSentences should separate paragraphs by line breaks`() {
        var content =
            "The spirit became furious, \"You lier, I'll teach you a lesson\"\nA splash of water burst from the lake and drenched Chilseong."
        var result = ContentParser.parseContentToSentences(content)
        val desired = listOf(
            "The spirit became furious, \"You lier, I'll teach you a lesson\"",
            "A splash of water burst from the lake and drenched Chilseong."
        )
        assertEquals(desired, result)
        content = content.replace("\n", "\r")
        result = ContentParser.parseContentToSentences(content)
        assertEquals(desired, result)
    }

    @Test
    fun `tokenizeSpecialContent should tokenize quotes correctly`() {
        val tokenizedParts = mutableListOf<String>()
        val result = ContentParser.tokenizeSpecialContent(
            "He said \"hello world\" and 'goodbye'.",
            tokenizedParts
        )

        assertEquals(2, tokenizedParts.size)
        assertEquals("\"hello world\"", tokenizedParts[0])
        assertEquals("'goodbye'", tokenizedParts[1])
        assertEquals("He said <<TOKEN_0>> and <<TOKEN_1>>.", result)
    }

    @Test
    fun `tokenizeSpecialContent should tokenize abbreviations correctly`() {
        val tokenizedParts = mutableListOf<String>()
        val result = ContentParser.tokenizeSpecialContent(
            "Dr. Smith went to U.S.A. yesterday.",
            tokenizedParts
        )

        assertEquals(2, tokenizedParts.size)
        assertEquals("Dr.", tokenizedParts[0])
        assertEquals("U.S.A.", tokenizedParts[1])
        assertEquals("<<TOKEN_0>> Smith went to <<TOKEN_1>> yesterday.", result)
    }

    @Test
    fun `tokenizeSpecialContent should tokenize decimal numbers correctly`() {
        val tokenizedParts = mutableListOf<String>()
        val result = ContentParser.tokenizeSpecialContent(
            "I have 2.5 dollars and paid 3.14 for coffee.",
            tokenizedParts
        )

        assertEquals(2, tokenizedParts.size)
        assertEquals("2.5", tokenizedParts[0])
        assertEquals("3.14", tokenizedParts[1])
        assertEquals("I have <<TOKEN_0>> dollars and paid <<TOKEN_1>> for coffee.", result)
    }

    @Test
    fun `normalizeWhitespace should normalize multiple spaces and tabs`() {
        val result = ContentParser.normalizeWhitespace("Hello    world\t\ntest")
        assertEquals("Hello world test", result)
    }

    @Test
    fun `isPurelyPunctuation should identify punctuation-only text`() {
        assertTrue(ContentParser.isPurelyPunctuation("... !!! ???"))
        assertTrue(ContentParser.isPurelyPunctuation("!"))
        assertFalse(ContentParser.isPurelyPunctuation("Hello!"))
        assertFalse(ContentParser.isPurelyPunctuation("Test."))
    }

    @Test
    fun `restoreTokens should replace tokens with original text`() {
        val tokenizedParts = listOf("\"hello\"", "2.5")
        val result = ContentParser.restoreTokens(
            "He said <<TOKEN_0>> and paid <<TOKEN_1>> dollars.",
            tokenizedParts
        )
        assertEquals("He said \"hello\" and paid 2.5 dollars.", result)
    }
}