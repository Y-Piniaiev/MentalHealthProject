import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Note(
    var _id: String,
    var title: String,
    var text: String,
)