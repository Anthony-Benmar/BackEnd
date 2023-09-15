package com.bbva.database.mappers;

import com.bbva.entities.spp.Period;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PeriodMapper {

    final String SQL_QUERY_PERIOD = "SELECT per.PeriodId, DATE_FORMAT(per.StartMeetUp,'%d-%m-%Y') as 'StartMeetUp', DATE_FORMAT(per.EndMeetUp,'%d-%m-%Y') as EndMeetUp,"+
            "DATE_FORMAT(per.StartDemand,'%d-%m-%Y') as 'StartDemand',DATE_FORMAT(per.EndDemand,'%d-%m-%Y') as 'EndDemand',"+
            "DATE_FORMAT(per.StartStaffing,'%d-%m-%Y') as 'StartStaffing',DATE_FORMAT(per.EndStaffing,'%d-%m-%Y') as 'EndStaffing',"+
            "per.Rate,per.Hours,per.CatalogStatus,cat.StatusName "+
            "FROM Period per INNER JOIN Catalog cat on per.CatalogStatus= cat.CatalogId "+
            "ORDER BY per.StartMeetUp;";

    @Select(SQL_QUERY_PERIOD)
    List<Period> getListPeriods();
}
