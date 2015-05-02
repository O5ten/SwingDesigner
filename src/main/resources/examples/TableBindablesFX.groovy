@Canonical
class Person {
    @FXBindable String firstName
    @FXBindable String lastName
    @FXBindable String city
    @FXBindable String state
}


def main() {
    def sg = new SceneGraphBuilder()

    def data = [
            new Person('Jim', 'Clarke', 'Orlando', 'FL'),
            new Person('Jim', 'Connors', 'Long Island', 'NY'),
            new Person('Eric', 'Bruno', 'Long Island', 'NY'),
            new Person('Dean', 'Iverson', 'Fort Collins', 'CO'),
            new Person('Jim', 'Weaver', 'Marion', 'IN'),
            new Person('Stephen', 'Chin', 'Belmont', 'CA'),
            new Person('Weiqi', 'Gao', 'Ballwin', 'MO'),
    ]

    sg.stage(title: "GroovyFX TableView Demo", visible: false) {
        scene(fill: groovyblue, width: 650, height:450) {
            stackPane(padding: 20) {
                tableView(items: data) {
                    tableColumn(text: "First Name", property: 'firstName')
                    tableColumn(text: "Last Name", property: 'lastName')
                    tableColumn(text: "City", property: 'city')
                    tableColumn(text: "State", property: 'state')
                }
            }
        }
    }
}