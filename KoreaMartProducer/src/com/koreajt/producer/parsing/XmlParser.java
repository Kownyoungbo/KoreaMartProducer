package com.koreajt.producer.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XmlParser {
	
	ArrayList<String> menuText;
	/**
	 * @author xml파서
	 * @param inputstream
	 * @return ArrayList<String>
	 * inputstream 을 받아서 파싱을 하는 메소드 
	 */
	public ArrayList<String> XmlParserOut(InputStream is){
		try {
			menuText = new ArrayList<String>();
			boolean[] initem = {false, false, false, false, false, false, false};
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(is, "UTF-8");
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
				case XmlPullParser.END_DOCUMENT:
				case XmlPullParser.END_TAG:
					break;
				case XmlPullParser.START_TAG:
					//사용자 이름
					if (xpp.getName().equals("name")) {
						initem[0] = true;
						initem[1] = false;
						initem[2] = false;
						initem[3] = false;
						initem[4] = false;
						initem[5] = false;
						initem[6] = false;
					}
					//사용자등급
					else if (xpp.getName().equals("level")) {
						initem[0] = false;
						initem[1] = true;
						initem[2] = false;
						initem[3] = false;
						initem[4] = false;
						initem[5] = false;
						initem[6] = false;
					}
					//신규주문
					else if (xpp.getName().equals("new")) {
						initem[0] = false;
						initem[1] = false;
						initem[2] = true;
						initem[3] = false;
						initem[4] = false;
						initem[5] = false;
						initem[6] = false;
					}
					//발송요정
					else if (xpp.getName().equals("send")) {
						initem[0] = false;
						initem[1] = false;
						initem[2] = false;
						initem[3] = true;
						initem[4] = false;
						initem[5] = false;
						initem[6] = false;
					}
					//반품/취소요청
					else if (xpp.getName().equals("cancle")) {
						initem[0] = false;
						initem[1] = false;
						initem[2] = false;
						initem[3] = false;
						initem[4] = true;
						initem[5] = false;
						initem[6] = false;
					}
					//신규상품문의
					else if (xpp.getName().equals("new_q")) {
						initem[0] = false;
						initem[1] = false;
						initem[2] = false;
						initem[3] = false;
						initem[4] = false;
						initem[5] = true;
						initem[6] = false;
					}
					//정산알림
					else if (xpp.getName().equals("total")) {
						initem[0] = false;
						initem[1] = false;
						initem[2] = false;
						initem[3] = false;
						initem[4] = false;
						initem[5] = false;
						initem[6] = true;
					}
					
					break;
				//값을 가지고 오는 부눈
				case XmlPullParser.TEXT:
					if (initem[0]) {
						menuText.add(xpp.getText());
						initem[0] = false;
					}else if (initem[1]) {
						menuText.add(xpp.getText());
						initem[1] = false;
					}else if (initem[2]) {
						menuText.add(xpp.getText());
						initem[2] = false;
					}else if (initem[3]) {
						menuText.add(xpp.getText());
						initem[3] = false;
					}else if (initem[4]) {
						menuText.add(xpp.getText());
						initem[4] = false;
					}else if (initem[5]) {
						menuText.add(xpp.getText());
						initem[5] = false;
					}else if (initem[6]) {
						menuText.add(xpp.getText());
						initem[6] = false;
					}
					break;
				}
				eventType = xpp.next();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return menuText;
	}
}
