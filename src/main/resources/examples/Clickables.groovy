def main(){
    new SwingBuilder().
            panel( layout: new MigLayout('wrap','','')){
                JLabel aLabel = label(text:"Header 1")
                button text:"clickable", actionPerformed: { aLabel.text = 'Oh no! The Clickables was clicked!'}
            }
}