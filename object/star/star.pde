/*星を描画*/

void setup(){
  size(500,500);
  background(0);
}

void draw(){
  
  background(0);
  strokeWeight(2);
  stroke(124,252,0);
  noFill();
  
  drawStar(width/2,height/2,150,5);
   
}

/*星を描く*/
/*ox,oy:中心座標、r:中心点とトゲの頂点までの距離、vertexNum:頂点(星のトゲ)数*/
void drawStar(int ox,int oy,int r,int vertexNum){

  pushMatrix();
  translate(ox,oy);
  rotate(radians(-90));  //角度をずらして一番上の部分を初期位置とする
  
  beginShape();
  //星を構成する点を1°ずつ定義していって、線分でつなぐ
  for(int theta = 0; theta < 360; theta++){
    PVector pos = calcPos(r,theta,vertexNum);
    float x = pos.x;
    float y = pos.y;
    vertex(x,y);
  }
  endShape(CLOSE);
  
  popMatrix();
  
}

/* ここから下の計算式の参考元：https://slpr.sakura.ne.jp/qp/polygon-spirograph/(正多角形とスピログラフの数式）*/
/* 各頂点座標を計算 */
/* r:中心点と頂点間の距離、theta:0～359、num:頂点数*/
PVector calcPos(int r,int theta,int num){
  float x = r * cos(radians(theta)) * func(theta,num);
  float y = r * sin(radians(theta)) * func(theta,num);
  println("theta = " + theta + " : x = " + x + " : y = " + y);
  return new PVector(x,y);
}

/*theta:0～359、num:頂点数*/
float func(int theta, int num){
  float a = 360/num;
  float A = cos(radians(a));
  float b = acos(cos(radians(num * theta)));    //acos:逆関数　これを使ってnum*tが360を超えている場合は360の範囲内に収めることができる
  float B = cos(radians(a) - b / num);
  
  return A/B;
}
