/*画像*/
class Image{
  PImage playerImg;
  PImage bossImg;
  PImage playerCutImg;
  PImage ufoImg;
  
  Image(){
    playerImg = loadImage("graphic/player.png");
    bossImg = loadImage("graphic/boss.png");
    playerCutImg = loadImage("graphic/playerCut.png");
    ufoImg = loadImage("graphic/ufo.png");
  }
  
  //**自機グラ描画
  void drawPlayerImage(PVector location){
    float x = location.x - Const.PLAYER_GRAPHIC_WIDTH/2;
    float y = location.y - Const.PLAYER_GRAPHIC_HEIGHT*2/3;
    image(playerImg,x,y);
  }
  
  //**ボスグラ描画
  void drawBossImage(PVector location){
    float x = location.x - Const.BOSS_GRAPHIC_WIDTH/2;
    float y = location.y - Const.BOSS_GRAPHIC_HEIGHT*2/3;
    image(bossImg,x,y);
  }

  //**UFOグラ描画
  void drawUFOImage(PVector location){
    float x = location.x - Const.UFO_GRAPHIC_WIDTH/2;
    float y = location.y - Const.UFO_GRAPHIC_HEIGHT*2/3;
    image(ufoImg,x,y);
  }

  //**自機グラ(会話シーン)描画
  void drawPlayerCutImage(PVector location){
    image(playerCutImg,location.x,location.y);
  }
  
  //**ボスグラ(会話シーン)描画
  void drawBossCutImage(PVector location){
    image(bossImg,location.x,location.y,Const.BOSSCUT_GRAPHIC_WIDTH,Const.BOSSCUT_GRAPHIC_HEIGHT);
  }
 
}
