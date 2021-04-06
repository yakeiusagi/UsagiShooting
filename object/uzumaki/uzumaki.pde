/* 渦巻き */

void setup(){

  size(500,500);
  background(234,244,252);
  
}

void draw(){
  stroke(0,123,67);
  strokeWeight(4);
  noFill();  
  translate(width/2,height/2);
  
  /* 渦巻きを描画(半円の連続) */
  int diameter = 25;
  int diameter_init = diameter;
  int loopNum = 20;
  for(int i=1;i<=loopNum;i++){
    if(i%2 == 1){
       //上半分の円 
       arc(0,0,diameter,diameter,PI,2*PI);
    }else{
       //下半分の円
       arc(-1*diameter_init/4,0,diameter,diameter,0,PI);  
    }
    diameter += diameter_init/2;
  }

}
