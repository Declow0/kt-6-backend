package ru.netology.backend.model.dto.rq

import org.hibernate.validator.constraints.URL
import ru.netology.backend.config.youTubeIDPatternString
import ru.netology.backend.model.Location
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Pattern

data class PostRqDto(
    @field:NotEmpty
    val content: String = "",

    val address: String = "",
    val location: Location? = null,

    @field:Pattern(regexp = youTubeIDPatternString, message = "{ru.netology.backend.model.dto.youtube.message}")
    val youtubeId: String? = null,

    @field:URL
    val commercialContent: String? = null
)
