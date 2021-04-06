/*タイトル画面描画*/
class Title{
  
  PVector[] location;
  float[] size;
  color[] col;
  
  Title(){
    location = new PVector[6];
    for(int i=0;i<6;i++){
      location[i] = new PVector(0,0);
    }
    size = new float[6];
    col = new color[6];
  }
  
  //**タイトル画面を再生
  void drawTitle(){
    /*背景描画*/
    
    noStroke();
    /*円状に星を描く*/
    for(int i=0; i<6; i++){
      fill(col[i]);
      pushMatrix();
      //描画位置をランダム生成
      translate(location[i].x,location[i].y);
      //星を描画
      int num = 12;
      for(int j = 0; j < num; j++){
        float ox = 180 * cos(radians(j * 360/num));
        float oy = 180 * sin(radians(j * 360/num));        
        drawStar(ox,oy,floor(size[i]),5);  //<>//
      }
      popMatrix();
    }
    
    /*メッセージ描画*/
    fill(255);
    textSize(30);
    text("pless Enter to start...",50,370);
  }
  
  //**初期化(背景描画用)
  void init(){
    for(int i=0; i<6; i++){
      location[i].x = random(600);
      location[i].y = random(100,750);
      size[i] = random(30,120);
    }
    
    col[0] = color(124,252,0,50);
    col[1] = color(255,228,225,50);
    col[2] = color(0,172,154,50);
    col[3] = color(255,220,0,50);
    col[4] = color(255,255,255,50);
    col[5] = color(234,85,80,50);
    
    //println(""); //<>//
  }
  
}
