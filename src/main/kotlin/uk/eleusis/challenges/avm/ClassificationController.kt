package uk.eleusis.challenges.avm

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ClassificationController(
        private val mc: MasterClassifier
) {

    @RequestMapping("/classify")
    fun classify(@RequestParam() word: String) = mc.classify(word)
}