/*正N角形を描画*/

void setup(){
  size(500,500);
  background(0);
}

void draw(){
  background(0);
  
  noFill();
  strokeWeight(2);
  stroke(124,252,0);
  
  //drawPolygon(150,6);
  drawPolygon2(150,6);
}

/*正多角形を描画*/
/* r:中心から頂点までの距離、num:頂点数 */
void drawPolygon(int r,int num){
  pushMatrix();
  translate(width/2,height/2);
  
  //頂点の数だけ座標をvertexで定義
  beginShape();
  for(int theta = 0; theta < num; theta++){
    float x = r * cos(radians(theta * 360/num));
    float y = r * sin(radians(theta * 360/num));
    println("theta = " + theta + " x = " + x + " y = " + y);
    vertex(x,y);
    ellipse(x,y,10,10);
  }
  endShape(CLOSE);
  
  popMatrix();
}

/*---------------------------------------------------------------------*/
/* 正多角形を描画 */
/* r:中心から頂点までの距離、num:頂点数 */
void drawPolygon2(int r,int num){
  pushMatrix();
  translate(width/2,height/2);
  
  /* 1度ずつ頂点を定義*/
  beginShape();
  for(int theta = 0; theta < 360; theta++){
    PVector pos = calcPos(r,theta,num);
    float x = pos.x;
    float y = pos.y;
    vertex(x,y);
    //ellipse(x,y,10,10);
  }
  endShape(CLOSE);
  
  popMatrix();
}

/* ここから下の計算式の参考元：https://slpr.sakura.ne.jp/qp/polygon-spirograph/(正多角形とスピログラフの数式）*/
/* r:中心から頂点までの距離、theta:0～359、num:頂点数 */
PVector calcPos(int r,int theta,int num){
  float x = r * cos(radians(theta)) * func(theta,num);
  float y = r * sin(radians(theta)) * func(theta,num);
  return new PVector(x,y);
}

/* r:中心から頂点までの距離、theta:0～359、num:頂点数 */
float func(int theta,int num){
  float A = cos(radians(180/num)); 
  float b = 360/num;
  float tb = theta/b - floor(theta/b);  //theta/bの小数点部分
  float B = cos(radians(b * tb - 180/num));
  return A/B;
}
