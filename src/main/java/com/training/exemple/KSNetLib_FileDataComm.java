package com.training.exemple;

import ksnetlib.filedatacomm.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class KSNetLib_FileDataComm
{
	public static void main(String[] args)
	{
		// ??? ????? ????
		
		KSNetLib ksnetlib = new KSNetLib();	
		
		//????????? ??? ?��??? ??��? ??????(??????? ???? ?��??? ??????? ???????, ?????? ??��? ??????)
		ksnetlib.SetLogPath("C://Log");
		
		//LOG ?????? ?????(???????? ?��?????? ???? ????? ??? ????)
		ksnetlib.EnableLog(1);		

		//????? Sleep ????
		ksnetlib.SetSleepTime(100000);      //????????? ?????? ????(????? 0.1??? ????)1/1000000(?????? 1??)
                                                    //???????? ???? ??? ?? ?????? ??? ????
                                                    //???? or ????+?????? ????? ??? ????? ??? ???? ???? ?? ???			
                                                    //??????(0.1??)
		// ???? ?????? ???
/*		int sendResult = ksnetlib.sendFileData(
				3,							// ???? :1 , ????? : 0
				//=======IP , PORT?? ?????? ???? ======== 
				"210.181.29.37",			// ???? ??????
				30189,						// ???? ???
				30000,						// ????, ????? ?????
				"./sendData/01_20110816.req",		// ??��? ?????? ???? ?????
				'T',						// ???? ??? (???? ????)
				200,						// ???? ??? ???? ?? ????? ??? (???? ????)
				"TESTMER001",				// ??? ???
				1,							// ????
				"110812"					// ??? ???? (YYMMDD)
				);
		
		System.out.println( ksnetlib.getErrorMessage(sendResult) );
		*/
		
		// ???? ?????? ????
		int recvResult = ksnetlib.recvFileData(
				1,							// ???? :1 , ????? : 0
				//=======IP , PORT?? ?????? ???? ======== 
				//"210.181.29.207",			// ???? ??????
				"210.181.28.207",			// ???? ??????
				20280,						// ???? ???
				10000,						// ????, ????? ?????
				"./recvData/221123.txt",	// ???? ???? ??????? ?????? ??��? ?????? ?????
				'T',						// ???? ??? (???? ????)
				"AT0214911A",				// ??? ???
				"REPLY",					// ??? ???? ??? (???? ????)
				150,						// ??? ???? ??? ???? ?? ????? ??? (???? ????)
				"221123"					// ???? ???? (YYMMDD)
				);
		
		System.out.println( ksnetlib.getErrorMessage(recvResult) );
	}
}
