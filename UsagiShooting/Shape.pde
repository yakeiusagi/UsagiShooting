/*図形の描画用(グローバル関数)*/

/*星型多角形を描く*/
/*ox,oy:中心座標、r:中心点とトゲの頂点までの距離、vertexNum:頂点(星のトゲ)数*/
void drawStar(float ox,float oy,int r,int vertexNum){

  pushMatrix();
  translate(ox,oy);
  rotate(radians(-90));  //角度をずらして一番上の部分を初期位置とする
  
  beginShape();
  //星を構成する点を1°ずつ定義していって、線分でつなぐ
  for(int theta = 0; theta < 360; theta++){
    PVector pos = calcPosStar(r,theta,vertexNum);
    float x = pos.x;
    float y = pos.y;
    vertex(x,y);
  }
  endShape(CLOSE);
  
  popMatrix();
}

/* 各頂点座標を計算 */
/* r:中心点と頂点間の距離、theta:0～359、num:頂点数*/
PVector calcPosStar(int r,int theta,int num){
  float x = r * cos(radians(theta)) * funcStar(theta,num);
  float y = r * sin(radians(theta)) * funcStar(theta,num);
  //println("theta = " + theta + " : x = " + x + " : y = " + y);
  return new PVector(x,y);
}

/*theta:0～359、num:頂点数*/
float funcStar(int theta, int num){
  float a = 360/num;
  float A = cos(radians(a));
  float b = acos(cos(radians(num * theta)));    //acos:逆関数　これを使ってnum*tが360を超えている場合は360の範囲内に収めることができる
  float B = cos(radians(a) - b / num);
  
  return A/B;
}
