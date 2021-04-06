/* オープニング画面クラス */
class Opening{
  
  private int count;    //カウントダウン変数
  private int fCount;  //フレームカウント変数
  
  //**コンストラクタ
  Opening(){
    count = 3;
    fCount = 0;
  }
  
  //**オープニングを描画
  int drawOpening(){
    
    //カウントダウン変数を表示
    int size = 100;
    fill(255);
    textSize(size);
    text(count,width/2-size/2,height/2);
    
    //フレーム数をカウントアップ
    fCount += 1;
    
    //1秒経ったら、カウント変数を1マイナス
    if(fCount%60 == 0){
      count -= 1;
    }
    
    return count;
    
  }
  
  //**カウントダウンをリセット
  void resetCount(){
    count = 3;
  }
  
}
