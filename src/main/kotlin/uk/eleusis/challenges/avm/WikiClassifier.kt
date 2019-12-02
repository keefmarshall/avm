package uk.eleusis.challenges.avm

import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import org.springframework.web.util.UriComponentsBuilder
import uk.eleusis.challenges.avm.Classification.*

@Component
@Order(5)
class WikiClassifier : Classifier {

    private val wikipediaApiUri = "https://en.wikipedia.org/w/api.php"

    override fun classify(word: String): Classification {
        val article = findArticle(word) ?: return UNKNOWN

        return when {
            hasPersonArticleTemplates(article.pageId) -> ANIMAL
            hasAnimalArticleTemplates(article.pageId) -> ANIMAL
            hasAnimalCategories(article.pageId) -> ANIMAL
            hasPlantArticleTemplates(article.pageId) -> VEGETABLE
            hasPlantCategories(article.pageId) -> VEGETABLE
            hasTaxonomyTemplate(article.pageId) -> classifyFromTaxoboxHTML(article.pageId)
            else -> UNKNOWN
        }
    }


    data class WikiArticleMeta(val pageId: String, val title: String)

    private val personTemplates = arrayOf("Template:Birth_date", "Template:Birth_date_and_age").joinToString("|")
    private val animalTemplates = arrayOf("Template:Infobox animal", "Template:Taxonomy/Animalia").joinToString("|")
    private val plantTemplates = arrayOf("Template:PLANTS", "Template:Plant taxa category", "Template:Taxonomy/Plantae").joinToString("|")

    fun hasPersonArticleTemplates(pageid: String) = hasTemplates(pageid, personTemplates)
    fun hasAnimalArticleTemplates(pageid: String)= hasTemplates(pageid, animalTemplates)
    fun hasPlantArticleTemplates(pageid: String)= hasTemplates(pageid, plantTemplates)
    fun hasTaxonomyTemplate(pageid: String) = hasTemplates(pageid, "Template:Taxonomy|Template:Taxobox")

    /**
     * https://en.wikipedia.org/w/api.php?action=query&pageids=12153654&prop=templates&tltemplates=Template:Birth_date|Template:Birth_date_and_age
     */
    fun hasTemplates(pageid: String, templates: String): Boolean {
        val uriBuilder = UriComponentsBuilder.fromHttpUrl(wikipediaApiUri)
                .queryParam("action", "query")
                .queryParam("pageids", pageid)
                .queryParam("prop", "templates")
                .queryParam("tltemplates", templates)
                .queryParam("format", "json")

        val response = RestTemplate().getForObject<ObjectNode>(uriBuilder.build().toUriString())

        // if it doesn't have one of the templates there will be no 'templates' key
        val page = response.get("query")?.get("pages")?.get(pageid)

        return page?.has("templates") ?: false
    }

    private val animalCategories = arrayOf("Category:Mammals_and_humans", "Category:Mammals")
    private val plantCategories = arrayOf("Category:Plants", "Category:Flora", "Category:Mushroom_types")

    fun hasAnimalCategories(pageid: String): Boolean = hasCategories(pageid, animalCategories)
    fun hasPlantCategories(pageid: String): Boolean = hasCategories(pageid, plantCategories)

    fun hasCategories(pageid: String, categories: Array<String>): Boolean {
        val uriBuilder = UriComponentsBuilder.fromHttpUrl(wikipediaApiUri)
                .queryParam("action", "query")
                .queryParam("pageids", pageid)
                .queryParam("prop", "categories")
                .queryParam("clcategories", categories.joinToString("|"))
                .queryParam("cllimit", "250") // max number we can retrieve in a single request
                .queryParam("format", "json")

        val response = RestTemplate().getForObject<ObjectNode>(uriBuilder.build().toUriString())

        val page = response.get("query")?.get("pages")?.get(pageid)
        return page?.has("categories") ?: false
    }


    /**
     * NB only gets the first 250!
     */
    fun getCategories(pageid: String): List<String> {
        val uriBuilder = UriComponentsBuilder.fromHttpUrl(wikipediaApiUri)
                .queryParam("action", "query")
                .queryParam("pageids", pageid)
                .queryParam("prop", "categories")
                .queryParam("cllimit", "250") // max number we can retrieve in a single request
                .queryParam("format", "json")

        val response = RestTemplate().getForObject<ObjectNode>(uriBuilder.build().toUriString())

        val page = response.get("query")?.get("pages")?.get(pageid)
        return page?.get("categories")?.mapNotNull { it.get("title").asText() } ?: emptyList()
    }

    /**
     * https://en.wikipedia.org/w/api.php?action=query&titles=Queen%20Elizabeth%20II&redirects=true&format=json
     */
    fun findArticle(word: String): WikiArticleMeta? {

        val uriBuilder = UriComponentsBuilder.fromHttpUrl(wikipediaApiUri)
                .queryParam("action", "query")
                .queryParam("titles", word)
                .queryParam("redirects", "true")
                .queryParam("format", "json")

        val response = RestTemplate().getForObject<ObjectNode>(uriBuilder.build().toUriString())

        // {"batchcomplete":"","query":{"redirects":[{"from":"Queen Elizabeth II","to":"Elizabeth II"}],
        // "pages":{"12153654":{"pageid":12153654,"ns":0,"title":"Elizabeth II"}}}}

        val pages = response.get("query")?.get("pages") as ObjectNode
        val page = pages.take(1)[0]

        return when {
            page.has("pageid") ->
                    WikiArticleMeta(
                        page?.get("pageid")?.asText() ?: "",
                        page?.get("title")?.asText() ?: ""
                    )
            else -> null
        }
    }

    // https://en.wikipedia.org/w/api.php?action=query&prop=revisions&pageids=37800458&rvprop=content
    fun classifyFromRegnum(pageid: String): Classification {
        val uriBuilder = UriComponentsBuilder.fromHttpUrl(wikipediaApiUri)
                .queryParam("action", "query")
                .queryParam("pageids", pageid)
                .queryParam("prop", "revision")
                .queryParam("rvprop", "content")
                .queryParam("format", "json")

        val response = RestTemplate().getForObject<ObjectNode>(uriBuilder.build().toUriString())

        val page = response.get("query")?.get("pages")?.get(pageid)
        val content = page?.get("revisions")?.get(0)?.get("*")?.asText() ?: ""

        if (content.contains("Taxobox")) {
            return when {
                content.contains("regnum = [[Animal]]ia") -> ANIMAL
                content.contains("regnum = [[Animal]]ia") -> ANIMAL
                else -> UNKNOWN
            }
        }

        return UNKNOWN
    }

    // https://en.wikipedia.org/w/api.php?action=parse&prop=text&section=0&page=Larix%20decidua
    fun classifyFromTaxoboxHTML(pageid: String): Classification {
        val uriBuilder = UriComponentsBuilder.fromHttpUrl(wikipediaApiUri)
                .queryParam("action", "parse")
                .queryParam("pageid", pageid)
                .queryParam("prop", "text")
                .queryParam("section", "0")
                .queryParam("format", "json")

        val response = RestTemplate().getForObject<ObjectNode>(uriBuilder.build().toUriString())

        val content = response.get("parse")?.get("text")?.get("*")?.asText() ?: ""
        return when {
            content.contains("Animalia") -> ANIMAL
            content.contains("Plantae") -> VEGETABLE
            content.contains("Chromista") -> VEGETABLE // Algae etc
            content.contains("Fungi") -> VEGETABLE
            content.contains("Protozoa") -> ANIMAL // Amoebas etc
            content.contains("Bacteria") -> ANIMAL
            content.contains("Archaea") -> ANIMAL // similar to Bacteria

            else -> UNKNOWN
        }
    }

}
