package uk.eleusis.challenges.avm

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class WikiClassifierTest {

    @Test
    fun classify() {
        assertEquals(Classification.ANIMAL, WikiClassifier().classify("Queen Elizabeth II"))
        assertEquals(Classification.ANIMAL, WikiClassifier().classify("Jean-Michel Jarre"))

        assertEquals(Classification.ANIMAL, WikiClassifier().classify("Greyfriars Bobby"))
        assertEquals(Classification.ANIMAL, WikiClassifier().classify("Elsa the Lioness"))
    }

    @Test
    fun hasPersonArticleTemplates() {
        assertTrue(WikiClassifier().hasPersonArticleTemplates("12153654"))
        assertFalse(WikiClassifier().hasPersonArticleTemplates("12153653"))
    }

    @Test
    fun getCategories() {
        val qe2Cats = WikiClassifier().getCategories("12153654")
        assertTrue(qe2Cats.size > 50)
    }

    @Test
    fun findArticle() {
        val wc = WikiClassifier()
        val article = wc.findArticle("Queen Elizabeth II")
        assertNotNull(article)
        assertEquals("12153654", article!!.pageId)
        assertEquals("Elizabeth II", article.title)
    }

    @Test
    fun findArticleThatDoesntExist() {
        val wc = WikiClassifier()
        val article = wc.findArticle("flibbledob")
        assertNull(article)
    }


}