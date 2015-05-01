def main(){
    new SceneGraphBuilder()
            .stage {
        scene {
            vbox(padding: 50){
                hbox(padding: 50) {
                    text(text: 'Groovy', font: '80pt sanserif'){
                        fill linearGradient(endX: 0, stops: ['#FF00FF','#FF0000'])
                        effect dropShadow(color: '#000000', radius: 25, spread: 0.25)
                    }
                    text(text: 'FX', font: '40pt sanserif', effect: dropShadow(color: DODGERBLUE, radius: 25, spread: 0.25))
                }
                def label
                def clickButton
                clickButton = button(text: 'Click me!', onAction: { label.visible = true; clickButton.disabled = true })
                label = text text: 'You are a button-clicker!', fill: '#FF0000', visible: false
            }
        }
    }
}