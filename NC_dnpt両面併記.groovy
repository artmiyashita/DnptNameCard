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

//段落自動取詰(上基準)メソッド
linespan = 0;//mm
lineheight = 0;//mm
positionY = 0;//mm
positionX = 0;//mm
def paragraphBuilder2(recordList,partsList,positionY,linespan,lineheight){
  x = recordList.size();
  for(i=0; i<x; i++){
    partsList[i].transform.translateY = positionY + linespan;
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

//ルビ生成メソッド
//jidoriNo=姓の場合:3,名の場合:4で変数を渡す
def rubyMaker(pNameRuby,nameRuby,pName,searchWord,rubySpan,rubySize,rubyFont,jidori,jidoriId,jidoriNo){
  pNameRuby.param.size = rubySize;
  pNameRuby.param.font = rubyFont;
  foundIndex = nameRuby.indexOf(searchWord);
  if (foundIndex < 0){
    //姓ルビ(センター)配置
    if(nameRuby){
      pNameRuby.editReferencePoint('lower-center',keepReferencePointPosition = false);
      pNameRuby.transform.translateX = pName.transform.translateX + pName.boundBox.width / 2;
      pNameRuby.param.maxWidth = pName.boundBox.width;
      pNameRuby.transform.translateY = pName.transform.translateY - rubySpan;
    }
  } else {
    //姓ルビ(モノルビ)配置
    //姓ルビ(モノルビ)を区切り文字”/”で分解、配列に追加
    def nameRubyList = [];
    while (foundIndex >= 0){
      nameRubyList.push(nameRuby.substring(0, foundIndex));
      nameRuby = nameRuby.substring(foundIndex+1);
      foundIndex = nameRuby.indexOf(searchWord);
    }
    nameRubyList.push(nameRuby);
    //姓ルビ(モノルビ)間の距離を算出
    def a = pName.param.size;
    def b = rubySize;
    def c = jidori[jidoriId][jidoriNo];
    def n = nameRubyList.size();
    def nameRubySpan = [];
    nameRubySpan[0] = (a - (b * nameRubyList[0].size()))/2;
    for (i=1; i<n; i++){
      nameRubySpan[i] = c + (2 * a - (b * (nameRubyList[i-1].size() + nameRubyList[i].size())))/2;
    }
    //姓ルビ(モノルビ)テキストの生成と配置
    def nameRubyText = '';
    for (i=0; i<n ; i++){
      nameRubyText += '<font size="' + nameRubySpan[i] + 'pt">　</font>' + nameRubyList[i];
    }
    pNameRuby.param.text = '<p>' + nameRubyText + '</p>';
    pNameRuby.editReferencePoint('lower-left',keepReferencePointPosition = false);
    pNameRuby.transform.translateX = pName.transform.translateX;
    pNameRuby.transform.translateY = pName.transform.translateY - rubySpan;
  }
  pNameRuby;
}


//独自の刺し込み処理
def myInjectionOne(cassette, record, labelList, imageTable) {

  def additionalLabelList = ['名称2','名称3','住所1結合','住所2結合',
  '電話番号A','電話番号B','電話番号C','電話番号D','電話番号英A','電話番号英B','電話番号英C','電話番号英D'];

  class1 = record['肩書き1'];
  class2 = record['肩書き2'];
  class3 = record['肩書き3'];
  class4 = record['肩書き4'];
  classEn1 = record['肩書き英字1'];
  classEn2 = record['肩書き英字2'];
  classEn3 = record['肩書き英字3'];
  classEn4 = record['肩書き英字4'];
  sei = record['姓'];
  mei = record['名'];
  seiruby = record['姓ルビ'];
  meiruby = record['名ルビ'];

  addressType1 = record['名称1'];
  postnum1 = record['郵便番号1'];
  address1 = record['住所1'];
  address12 = record['住所1-2'];
  postnum2 = record['郵便番号2'];
  address2 = record['住所2'];
  address3 = record['住所3'];

  tel1 = record['電話番号1'];
  tel2 = record['電話番号2'];
  fax1 = record['FAX番号1'];
  fax2 = record['FAX番号2']
  mobile = record['携帯電話番号']
  email = record['E-mail'];

  if(tel1){
    telEn1 = "+81-" + tel1.substring(0,tel1.size());
  }else{
    telEn1 = "";
  }
  if(tel2){
    telEn2 = "+81-" + tel2.substring(0,tel2.size());
  }else{
    telEn2 = "";
  }
  if(fax1){
    faxEn1 = "+81-" + fax1.substring(0,fax1.size());
  }else{
    faxEn1 = "";
  }
  if(fax2){
    faxEn2 = "+81-" + fax2.substring(0,fax2.size());
  }else{
    faxEn2 = "";
  }


  if(addressType1 == "なし"){
    //行数判定用電話結合
    tel1unit = tel1 + tel2;
    tel2unit = fax1 + fax2;
    //住所結合
    record['住所1結合'] = postnum1 + ' ' + address1;
    //電話番号の配置
    record['電話番号A'] = 'TEL:' + tel1;
    record['電話番号B'] = '    ' + tel2;//フォント次第でずれる可能性あり
    record['電話番号C'] = 'FAX:' + fax1;
    record['電話番号D'] = 'FAX:' + fax2;

    record['電話番号英A'] = 'TEL:' + telEn1;
    record['電話番号英B'] = '    ' + telEn2;//フォント次第でずれる可能性あり
    record['電話番号英C'] = 'FAX:' + faxEn1;
    record['電話番号英D'] = 'FAX:' + faxEn2;

  } else {
    //行数判定用電話結合
    tel1unit = tel1 + fax1;
    tel2unit = tel2 + fax2;
    if(tel1 == "" && mobile != ""){
      tel1unit = mobile + fax1;
    }
    //住所結合
    record['住所1結合'] = postnum1 + ' ' + address1;
    if(address12.size()>0){
      record['住所1結合'] = record['住所1結合'] + ' ' + address12;
    }
    record['住所2結合'] = postnum2 + ' ' + address2;
    if(address3.size()>0){
      record['住所2結合'] = record['住所2結合'] + ' ' + address3;
    }
    //電話番号の配置
    record['電話番号A'] = 'TEL:' + tel1;
    record['電話番号B'] = 'FAX:' + fax1;
    record['電話番号C'] = 'TEL:' + tel2;
    record['電話番号D'] = 'FAX:' + fax2;
    if(tel1 == "" && mobile != ""){
      record['電話番号A'] = '携帯:' + mobile;
    }
    record['電話番号英A'] = 'TEL:' + telEn1;
    record['電話番号英B'] = 'FAX:' + faxEn1;
    record['電話番号英C'] = '';
    record['電話番号英D'] = '';
    if(tel1 == "" && mobile != ""){
      record['電話番号英A'] = 'TEL:' + mobile;
    }
  }

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
    pAddressUnit2 = getPartsByLabel('住所2結合',1,cassette);

    pTelA = getPartsByLabel('電話番号A',1,cassette);
    pTelB = getPartsByLabel('電話番号B',1,cassette);
    pTelC = getPartsByLabel('電話番号C',1,cassette);
    pTelD = getPartsByLabel('電話番号D',1,cassette);

    pTel1Type = getPartsByLabel('電話1種別',1,cassette);
    pTel2Type = getPartsByLabel('電話2種別',1,cassette);

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

    //ルビの設定
    def rubySize = 5;//ルビの文字サイズ指定(pt)
    def rubyFont = 'FOT-ロダン Pro M';//ルビのフォント
    def rubySpan = -2;//ルビと氏名の距離
    searchWord = '/';
    //姓ルビの関数
    pSeiRuby = rubyMaker(pSeiRuby,seiruby,pSei,searchWord,rubySpan,rubySize,rubyFont,jidori,jidoriId,3)
    //名ルビの関数
    pMeiRuby = rubyMaker(pMeiRuby,meiruby,pMei,searchWord,rubySpan,rubySize,rubyFont,jidori,jidoriId,4)

    //肩書配置
    recordList = [class1,class2,class3];
    partsList = [pClass1,pClass2,pClass3];
    linespan = 0;
    lineheight = 2.82;
    positionY = pSei.transform.translateY - pSei.boundBox.height - 1.5;
    paragraphBuilder(recordList,partsList,positionY,linespan,lineheight);
    pClass4.transform.translateY = pMeiRuby.transform.translateY + 0.5;

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

    //住所配置
    if(addressType1 == "なし"){
      pAddressUnit2.setDisplay('none');
      recordList = [addressType1,address1,address12,tel1unit,tel2unit,email];
      partsList = [pAddressType1,pAddressUnit1,pAddress12,pTelA,pTelC,pEmail];
      linespan = 0;
      lineheight = 2.75;
      positionY = 55 - 7.68;
      paragraphBuilder(recordList,partsList,positionY,linespan,lineheight);
    }else{
      pAddress12.param.text = '';
      recordList = [addressType1,address1,tel1unit,address2,tel2unit,email];
      partsList = [pAddressType1,pAddressUnit1,pTelA,pAddressUnit2,pTelC,pEmail];
      linespan = 0;
      lineheight = 2.75;
      positionY = 55 - 7.68;
      paragraphBuilder(recordList,partsList,positionY,linespan,lineheight);
    }

    //電話番号配置
    pTelB.transform.translateX = 60;
    pTelB.transform.translateY = pTelA.transform.translateY;
    pTelD.transform.translateX = 60;
    pTelD.transform.translateY = pTelC.transform.translateY;

    if(addressType1 == "なし"){
      pTel1Type.transform.translateX = pTelA.transform.translateX + pTelA.boundBox.width;
      pTel1Type.transform.translateY = pTelA.transform.translateY;
      pTel2Type.transform.translateX = pTelB.transform.translateX + pTelB.boundBox.width;
      pTel2Type.transform.translateY = pTelB.transform.translateY;
    } else {
      pTel1Type.transform.translateX = pTelA.transform.translateX + pTelA.boundBox.width;
      pTel1Type.transform.translateY = pTelA.transform.translateY;
      pTel2Type.transform.translateX = pTelC.transform.translateX + pTelC.boundBox.width;
      pTel2Type.transform.translateY = pTelC.transform.translateY;
    }


    //非表示処理
    if(addressType1 == "なし"){
      if(tel1==''){
        pTelA.setDisplay('none');
        pTel1Type.setDisplay('none');
      }
      if(tel2==''){
        pTelB.setDisplay('none');
        pTel2Type.setDisplay('none');
      }
      if(fax1==''){
        pTelC.setDisplay('none');
      }
      if(fax2==''){
        pTelD.setDisplay('none');
      }
    } else {
      if(tel1==''){
        if(mobile ==''){
          pTelA.setDisplay('none');
        }
        pTel1Type.setDisplay('none');
      }
      if(tel2==''){
        pTelC.setDisplay('none');
        pTel2Type.setDisplay('none');
      }
      if(fax1==''){
        pTelB.setDisplay('none');
      }
      if(fax2==''){
        pTelD.setDisplay('none');
      }
    }

  }else{
    pClassEn1 = getPartsByLabel("肩書き英字1",1,cassette);
    pClassEn2 = getPartsByLabel("肩書き英字2",1,cassette);
    pClassEn3 = getPartsByLabel("肩書き英字3",1,cassette);
    pClassEn4 = getPartsByLabel("肩書き英字4",1,cassette);
    pTelEnA = getPartsByLabel("電話番号英A",1,cassette);
    pTelEnB = getPartsByLabel("電話番号英B",1,cassette);
    pTelEnC = getPartsByLabel("電話番号英C",1,cassette);
    pTelEnD = getPartsByLabel("電話番号英D",1,cassette);

    pTelEnB.transform.translateX = pTelEnA.transform.translateX + pTelEnA.boundBox.width;
    pTelEnB.transform.translateY = pTelEnA.transform.translateY;
    pTelEnD.transform.translateX = pTelEnC.transform.translateX + pTelEnC.boundBox.width;
    pTelEnD.transform.translateY = pTelEnC.transform.translateY;

    pTest = getPartsByLabel("test",1,cassette);
    pTest.param.text = pClassEn1.transform.translateY;


    recordList = [classEn1,classEn2,classEn3,classEn4];
    partsList = [pClassEn1,pClassEn2,pClassEn3,pClassEn4];
    linespan = 0;
    lineheight = 2.82;
    positionY = 20;
    paragraphBuilder2(recordList,partsList,positionY,linespan,lineheight);


    //住所配置
    if(addressType1 == "なし"){
      pAddressUnit2.setDisplay('none');
      recordList = [addressType1,address1,address12,tel1unit,tel2unit,email];
      partsList = [pAddressType1,pAddressUnit1,pAddress12,pTelA,pTelC,pEmail];
      linespan = 0;
      lineheight = 2.75;
      positionY = 55 - 7.68;
      paragraphBuilder(recordList,partsList,positionY,linespan,lineheight);
    }else{
      pAddress12.param.text = '';
      recordList = [addressType1,address1,tel1unit,address2,tel2unit,email];
      partsList = [pAddressType1,pAddressUnit1,pTelA,pAddressUnit2,pTelC,pEmail];
      linespan = 0;
      lineheight = 2.75;
      positionY = 55 - 7.68;
      paragraphBuilder(recordList,partsList,positionY,linespan,lineheight);
    }

  }
}
