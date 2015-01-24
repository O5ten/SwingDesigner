def main(){
    new SwingBuilder().
        panel(layout: new MigLayout('wrap 3','0[grow,fill]0[]0[grow,fill]0','0[grow,fill]0[]0[grow,fill]0')){
		panel background: blue
		panel background: yellow, constraints: 'w 100!'
		panel background: blue, constraints: 'w 300'
		panel background: yellow, constraints: 'h 100!'
		panel background: yellow, constraints: 'w 100!, h 100!'
		panel background: yellow, constraints: 'h 100!'
		panel background: blue
		panel background: yellow, constraints: 'w 100!'
		panel background: blue, constraints: 'w 300'
	}
}