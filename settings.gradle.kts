rootProject.name = "JavaNotebooks"
include("processor", "core", "example")

sourceControl {
    gitRepository(uri("")) {
        producesModule("")
    }
}
