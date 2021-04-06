/*ゲームオーバー画面制御*/
class GameOver{
  
  int myFrameCount = 0;
  boolean countFlg = false;
  
  void drawGameOver(){
    fill(255);
    textSize(30);
    text("Game Over!",50,370);
    
    if(countFlg){
      myFrameCount++;
    }
    
    if(myFrameCount >= 120){
      text("Press Any Key To Retry",100,500);
      textSize(15);
      text("※リトライしない場合、画面を閉じてください",100,550);
      //リトライ
      if(keyPressed){
        countFlg = false;
        myFrameCount = 0;
        retry();
      }
    }
     
  }
  
  void enableCountFlg(){
    countFlg = true;
  }
  
}
