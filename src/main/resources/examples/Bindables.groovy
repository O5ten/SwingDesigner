def main(){
    B bindable = new B()
    new SwingBuilder().
            panel( layout: new MigLayout('wrap 3','20[grow, fill]8[grow, fill]8[grow,fill]20','0[]8[grow,fill]8[]8[]20')){
                label text: '<HTML><FONT COLOR="#000000" size="9">Bound Color Displayer', constraints: 'span 3'
                def a,b,c
                def pnl = panel( constraints: 'span 3', background: new Color(bindable.r, bindable.g, bindable.b), layout: new MigLayout('','0[grow, c]0','0[grow, c]0')){
                    a = label()
                    b = label()
                    c = label()
                }
                button text: 'Show Color', constraints: 'span 3', actionPerformed: {
                    pnl.background = new Color(bindable.r, bindable.g, bindable.b)
                    a.text = '<HTML><FONT COLOR="#FF0000" SIZE="9">Red'  + bindable.r
                    b.text = '<HTML><FONT COLOR="#00FF00" SIZE="9">Green ' + bindable.g
                    c.text = '<HTML><FONT COLOR="#0000FF" SIZE="9">Blue ' + bindable.b
                }
                label text: bind { '<HTML><FONT COLOR="#FF0000" SIZE="9">Red ' + bindable.r }
                label text: bind { '<HTML><FONT COLOR="#00FF00" SIZE="9">Green ' + bindable.g }
                label text: bind { '<HTML><FONT COLOR="#0000FF" SIZE="9">Blue ' + bindable.b }
                slider value:bind(target: bindable, targetProperty: 'r', converter: { v -> v*2.55})
                slider value:bind(target: bindable, targetProperty: 'g', converter: { v -> v*2.55})
                slider value:bind(target: bindable, targetProperty: 'b', converter: { v -> v*2.55})
            }
}

@Bindable
class B{
    int r = 127
    int g = 127
    int b = 127
}
