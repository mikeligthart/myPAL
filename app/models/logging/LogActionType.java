package models.logging;

/**
 * myPAL
 * Purpose: enum that contains the different types a LogAction can have
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
 */
public enum LogActionType {
    LOGIN, LOGOFF, ACCESSCALENDAR, ACCESSGOALS, ACCESSADDACTIVITYPAGE, ADDEDACTIVITY, ACCESSGALLERY, ACCESSADDPICTUREPAGE, VIEWACTIVITY, DELETEACTIVITY, DELETEPICTUREFROMACTIVITY, UNLINKPICTUREFROMACTIVITY, UPDATEACTIVITY, DELETEPICTUREFROMGALLERY, SELECTPICTUREFROMGALLRERYPAGE, ACCESSADDPICTUREDIRECTLYPAGE, ADDEDPICTUREDIRECTLY, LINKPICTURETOACTIVITY, UPDATECALENDARDOWN, UPDATECALENDARUP, UPDATECALENDARDIRECTLY, ACCESSSELECTMEASUREMENTPAGE, ACCESSADDGLUCOSEPAGE, ADDEDGLUCOSE, VIEWMEASUREMENT, ADDEDPICTURE, REMOVEDMEASUREMENT, UPDATEGLUCOSE, UPDATEINSULIN, UPDATEDGLUCOSE, UPDATEDINSULINE, UPDATEDCARBOHYDRATE, UPDATECARBOHYDRATE, ACCESSADDCARBOHYDRATEPAGE, ADDEDINSULIN, ADDEDCARBOHYDRATE, UPLOADEDPICTURE, ACCESSGOALADDDAILYPAGE, ADDEDGOALDAILY, ACCESSGOALADDTOTALPAGE, ADDEDGOALTOTAL, DELETEGOAL, ACCESSUPDATEACTIVITYPAGE, ACCESSADDACTIVITYTYPEPAGE, ACCESSADDINSULINPAGE, ACCESSUPDATEGLUCOSEPAGE, ACCESSUPDATEINSULINPAGE, ADDEDACTIVITYTYPE, DELETEACTIVITYTYPE, DELETEGLUCOSE, DELETEINSULIN, ADDEDWITHGLUCONLINE;
}
