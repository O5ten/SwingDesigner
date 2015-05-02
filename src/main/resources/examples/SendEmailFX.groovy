def main() {
    new SceneGraphBuilder()
            .stage {
        scene(fill: DODGERBLUE) {
            gridPane(hgap: 5, vgap: 10, padding: 25, alignment: "top_center") {
                columnConstraints(minWidth: 50, halignment: "right")
                columnConstraints(prefWidth: 250, hgrow: 'always')

                label("Please Send Us Your Feedback", style: "-fx-font-size: 18px;",
                        textFill: DODGERBLUE, row: 0, columnSpan: 2, halignment: "center",
                        margin: [0, 0, 10]) {
                    onMouseEntered { e -> e.source.parent.gridLinesVisible = true }
                    onMouseExited { e -> e.source.parent.gridLinesVisible = false }
                }

                label("Name", hgrow: "never", row: 1, column: 0, textFill: DODGERBLUE)
                def name = textField(promptText: "Your name", row: 1, column: 1)

                label("Email", row: 2, column: 0, textFill: DODGERBLUE)
                textField(promptText: "Your email address", row: 2, column: 1)

                label("Message", row: 3, column: 0, valignment: "baseline",
                        textFill: DODGERBLUE)
                textArea(promptText: 'Your feedback', prefRowCount: 8, row: 3, column: 1, vgrow: 'always')

                def label = text text: '', row: 4, column: 1, halignment: 'center'
                button(textFill: YELLOW, onAction: { event -> label.text = "Thank you for your Feedback, ${name.text ? name.text + ', ' : ''}it's very valuable!" }, style: '-fx-background-color: DODGERBLUE', "Send Message", row: 4, column: 1, halignment: "right")

            }
        }
    }
}
