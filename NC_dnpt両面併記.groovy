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

  class1 = record['肩書き1'];
  class2 = record['肩書き2'];
  class3 = record['肩書き3'];
  class4 = record['肩書き4'];
  sei = record['姓'];
  mei = record['名'];
  seiruby = record['姓ルビ'];
  meiruby = record['名ルビ'];

  addressType1 = record['名称1'];
  postnum1 = record['郵便番号1'];
  address1 = record['住所1'];
  address12 = record['住所1-2'];
  tel1 = record['電話番号1'];
  fax1 = record['FAX番号1'];

  postnum2 = record['郵便番号2'];
  address2 = record['住所2'];
  address3 = record['住所3'];
  tel2 = record['電話番号2'];
  fax2 = record['FAX番号2']
  email = record['E-mail'];

  //住所結合
  if(address12.size()>0){
    address12 = ' ' + address12;
  }
  record['住所1結合'] = postnum1 + ' ' + address1 + address12;

  if(address3.size()>0){
    address3 = ' ' + address3;
  }
  record['住所2結合'] = postnum2 + ' ' + address2 + address3;


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
    pClass1 = getPartsByLabel('肩書き1',1,cassette);
    pClass2 = getPartsByLabel('肩書き2',1,cassette);
    pClass3 = getPartsByLabel('肩書き3',1,cassette);
    pClass4 = getPartsByLabel('肩書き4',1,cassette);
    pSei = getPartsByLabel('姓', 1, cassette);
    pMei = getPartsByLabel('名', 1, cassette);
    pSeiRuby = getPartsByLabel('姓ルビ', 1, cassette);
    pMeiRuby = getPartsByLabel('名ルビ', 1, cassette);

    pAddressType1 = getPartsByLabel('名称1',1,cassette);
    pAddressType2 = getPartsByLabel('名称2',1,cassette);
    pAddressType3 = getPartsByLabel('名称3',1,cassette);

    pAddressUnit1 = getPartsByLabel('住所1結合',1,cassette);
    pAddress12 = getPartsByLabel('住所1-2',1,cassette);
    pTel1 = getPartsByLabel('電話番号1',1,cassette);
    pTel1Type = getPartsByLabel('電話1種別',1,cassette);
    pFax1 = getPartsByLabel('FAX番号1',1,cassette);

    pAddressUnit2 = getPartsByLabel('住所2結合',1,cassette);
    pTel2 = getPartsByLabel('電話番号2',1,cassette);
    pTel2Type = getPartsByLabel('電話2種別',1,cassette);
    pFax2 = getPartsByLabel('FAX番号2',1,cassette);
    pEmail = getPartsByLabel('E-mail',1,cassette);

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
    recordList = [class1,class2,class3];
    partsList = [pClass1,pClass2,pClass3];
    linespan = 0;
    lineheight = 2.82;
    positionY = pSei.transform.translateY - pSei.boundBox.height - 1.5;
    paragraphBuilder(recordList,partsList,positionY,linespan,lineheight);
    pClass4.transform.translateY = pMeiRuby.transform.translateY + 1;

    //住所配置
    pAddress12.param.text = '';
    recordList = [addressType1,address1,tel1,address2,tel2,email];
    partsList = [pAddressType1,pAddressUnit1,pTel1,pAddressUnit2,pTel2,pEmail];
    linespan = 0;
    lineheight = 2.75;
    positionY = 55 - 7.68;
    paragraphBuilder(recordList,partsList,positionY,linespan,lineheight);

    //電話番号配置
    pTel1Type.transform.translateX = pTel1.transform.translateX + pTel1.boundBox.width;
    pTel1Type.transform.translateY = pTel1.transform.translateY;
    pTel2Type.transform.translateX = pTel2.transform.translateX + pTel2.boundBox.width;
    pTel2Type.transform.translateY = pTel2.transform.translateY;

    //FAX配置
    pFax1.transform.translateX = 60;
    pFax1.transform.translateY = pTel1.transform.translateY;
    pFax2.transform.translateX = 60;
    pFax2.transform.translateY = pTel2.transform.translateY;

    //名称定義
    switch(addressType1){
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

    //非表示処理
    if(tel1==''){
      pTel1Type.setDisplay('none');
    }
    if(tel2==''){
      pTel2Type.setDisplay('none');
    }

  }
}
