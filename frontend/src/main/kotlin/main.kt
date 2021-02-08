import components.App
import kotlinx.browser.document
import react.dom.render

fun main() {
    kotlinext.js.require("bootstrap/dist/css/bootstrap.css")

    render(document.getElementById("root")) {
        child(App::class) {}
    }
}