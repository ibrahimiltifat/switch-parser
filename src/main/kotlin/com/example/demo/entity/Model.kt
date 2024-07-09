import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Model(
    val TranCode: String,
    val A: String,
    val C: List<Model>? = null,
    val F: Model? = null
)
