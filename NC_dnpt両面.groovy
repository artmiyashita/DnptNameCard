@import injectionValiable
env.injectionOne = {cassette, record, labelList, imageTable ->
 myInjectionOne(cassette, record, labelList, imageTable);
};
if(!env.multiLayout) {
 doProduce(2, env.imageTable); // ページ数 2
}
//関数定義
//段落自動取詰(下基準)メソッド
linespan = 0;//mm
lineheight = 0;//mm
positionY = 0;//mm
positionX = 0;//mm
def paragraphBuilder(recordList,partsList,positionY,linespan,lineheight){
  i = recordList.size() - 1;
  for(i; i>-1; i--){
    partsList[i].transform.translateY = positionY - linespan;
    if(recordList[i]==''){
      partsList[i].setDisplay("none");
      linespan += 0;
    }else{
      linespan += lineheight;
    }
  }
}
// 字取りメソッド
def jidoriBuilder(jidori,sei,mei,pSei,pMei,span,positionX){
  j = jidori.size() - 1;
  for(i=0; i<jidori.size(); i++){
    if(sei.length() == jidori[i][0] && mei.length() == jidori[i][1]){
      //合致パターンの記録
      jidoriId = i;
      //姓名間のスペースの倍率変更
      smspan = span * jidori[i][2];
      //姓名にスペース追加
      pSei.param.letterSpacing = jidori[i][3];
      pMei.param.letterSpacing = jidori[i][4];
      break;
    }else{
      smspan = span * jidori[j][2];
      pSei.param.letterSpacing = jidori[j][3];
      pMei.param.letterSpacing = jidori[j][4];
    }
  }
  pSei.transform.translateX = positionX;
  pMei.transform.translateX = positionX + pSei.boundBox.width + smspan;
}

//独自の刺し込み処理
def myInjectionOne(cassette, record, labelList, imageTable) {

  def additionalLabelList = ['名称2','名称3','住所1結合','住所2結合'];

  //基本関数
  labelList.each {
    injectionOneParts(cassette, it , record, imageTable);
  }

  //追加ラベルへの差し込み
  additionalLabelList.each {
    injectionOneParts(cassette, it , record, imageTable);
  }

  //表面の判定
  def omote = getPartsByLabel('肩書き1', 1, cassette) ;
  //表面の処理ここから
  if(omote != null){
    //表面のパーツ操作スクリプト
    //デフォルト設定
    sei = record['姓'] ;
    mei = record['名'] ;
    pSei = getPartsByLabel('姓', 1, cassette) ;
    pMei = getPartsByLabel('名', 1, cassette) ;
    //字取り定義
    positionX = 30;//mm
    def span = 5;//全角スペース1個分(mm)
    //Jidori＝[姓文字数、名文字数、姓名間全角スペース比、姓スペース(pt)、名スペース(pt)]
    def jidori = [
      [1,1,2,0,0],
      [1,2,1,0,3.54],
      [1,3,0.5,0,0],
      [2,1,1,3.54,0],
      [2,2,0.5,0,0],
      [2,3,0.5,0,0],
      [3,1,0.5,0,0],
      [3,2,0.5,0,0],
      [3,3,0.5,0,0],
      [0,0,0.5,0,0]
      ];
    jidoriBuilder(jidori,sei,mei,pSei,pMei,span,positionX);

    //肩書配置
    class1 = record['肩書き1'];
    class2 = record['肩書き2'];
    class3 = record['肩書き3'];
    recordList = [class1,class2,class3];
    pLastName = getPartsByLabel('姓',1,cassette);
    pClass1 = getPartsByLabel('肩書き1',1,cassette);
    pClass2 = getPartsByLabel('肩書き2',1,cassette);
    pClass3 = getPartsByLabel('肩書き3',1,cassette);
    partsList = [pClass1,pClass2,pClass3];
    linespan = 0;
    lineheight = 2.82;
    positionY = pLastName.transform.translateY - pLastName.boundBox.height - 1.5;
    paragraphBuilder(recordList,partsList,positionY,linespan,lineheight);

    //住所結合
    postnum1 = record['郵便番号1'];
    address1 = record['住所1'];
    pAddressUnit1 = getPartsByLabel('住所1結合',1,cassette);
    pAddressUnit1.param.text = '〒' + postnum1 + ' ' + address1;

    postnum2 = record['郵便番号2'];
    address2 = record['住所2'];
    pAddressUnit2 = getPartsByLabel('住所2結合',1,cassette);
    pAddressUnit2.param.text = '〒' + postnum2 + ' ' + address2;

    //電話番号配置
    pTel1 = getPartsByLabel('電話番号1',1,cassette);
    pTel1Type = getPartsByLabel('電話1種別',1,cassette);
    pTel1Type.transform.translateX = pTel1.transform.translateX + pTel1.boundBox.width;

    pTel2 = getPartsByLabel('電話番号2',1,cassette);
    pTel2Type = getPartsByLabel('電話2種別',1,cassette);
    pTel2Type.transform.translateX = pTel2.transform.translateX + pTel2.boundBox.width;

    //FAX配置
    pFax1 = getPartsByLabel('FAX番号1',1,cassette);
    pFax1.transform.translateX = 60;

    pFax2 = getPartsByLabel('FAX番号2',1,cassette);
    pFax2.transform.translateX = 60;

    //名称定義
    addressType = record['名称1'];
    pAddressType1 = getPartsByLabel('名称1',1,cassette);
    pAddressType2 = getPartsByLabel('名称2',1,cassette);
    pAddressType3 = getPartsByLabel('名称3',1,cassette);
    switch(addressType){
      case '京都駐在':
        pAddressType1.param.text = '大日本印刷(株)京都駐在';
        pAddressType2.param.text = '';
        pAddressType3.param.text = '';
      break;
      case '京都（田辺工場駐在）':
        pAddressType1.param.text = '大日本印刷(株)田辺工場駐在';
        pAddressType2.param.text = '';
        pAddressType3.param.text = '';
        break;
      case '関東関西併記':
        pAddressType1.param.text = '';
        pAddressType2.param.text = '本社';
        pAddressType3.param.text = '関西';
      break;
      case '福岡（筑後工場駐在）':
        pAddressType1.param.text = '(株)DNPテクノパック 筑後工場駐在';
        pAddressType2.param.text = '';
        pAddressType3.param.text = '';
      break;
      default:
        pAddressType1.param.text = '';
        pAddressType2.param.text = '';
        pAddressType3.param.text = '';
      break;
    }

  }
}
