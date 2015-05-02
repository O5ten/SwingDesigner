def main() {
    def circles
    new SceneGraphBuilder()
            .stage(title: 'GroovyFX ColorfulCircles', resizable: false, show: false) {
        scene(width: 800, height: 600, fill: 'black') {
            group {
                Random random = new Random(400)
                circles = group {
                    15.times {
                        circle(radius: 20, fill: rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 0.3f),
                                stroke: rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 0.8f),
                                strokeWidth: 8, strokeType: 'inside')
                    }
                    effect boxBlur(width: 10, height: 10, iterations: 1)
                }
                rectangle(width: 800, height: 600, blendMode: 'overlay') {
                    def stops = ['#f8bd55', '#FF0000', DODGERBLUE, RED]
                    fill linearGradient(start: [0f, 1f], end: [1f, 0f], stops: stops)
                }
            }
        }

        parallelTransition(cycleCount: 'indefinite', autoReverse: true) {
            def random = new Random()
            circles.children.each { circle ->
                translateTransition(30.s, node: circle, fromX: random.nextInt(800),
                        fromY: random.nextInt(600),
                        toX: random.nextInt(800),
                        toY: random.nextInt(600))
            }
        }.play()
    }
}
