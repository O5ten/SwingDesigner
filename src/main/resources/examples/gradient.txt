def main(){
    new SwingBuilder().
        panel(layout: new MigLayout('wrap 50','0[grow,fill]0','0[grow,fill]0')){
		50.times{ value -> 
			panel( background: [value, 255-value, value]){
							
       	     		}
		}
		50.times{ value -> 
			panel( background: [255-value, 0, value]){
							
       	     		}
		}
		50.times{ value -> 
			panel( background: [value, 0, value]){
							
       	     		}
		}
		50.times{ value -> 
			panel( background: [255-value, 255-value, value]){
							
       	     		}
		}
	}
}