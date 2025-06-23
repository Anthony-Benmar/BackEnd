package com.bbva.database.mappers;

import com.bbva.entities.issueticket.WorkOrder;
import com.bbva.entities.issueticket.WorkOrderDetail;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;

public interface IssueTicketMapper {
    @Select("CALL SP_BUC_PAGED_FILTERED(" +
            "#{page}," +
            "#{records_amount}," +
            "#{folio_code}," +
            "#{field_code}," +
            "#{project_name}," +
            "#{source_id}," +
            "#{priority}," +
            "#{resolucion_state}," +
            "#{functional_description})")
    ArrayList<WorkOrder> pagination(@Param("page") int page,
                                    @Param("records_amount") int records_amount,
                                    @Param("folio_code") String folio_code,
                                    @Param("field_code") String field_code,
                                    @Param("project_name") String project_name,
                                    @Param("source_id") Number source_id,
                                    @Param("priority") Integer priority,
                                    @Param("resolucion_state") Integer resolucion_state,
                                    @Param("functional_description") String functional_description);

    @Select("SELECT COUNT(jw.work_order_id) as result FROM jira_workorder jw " +
            "WHERE jw.feature = #{feature} " +
            "AND jw.folio = #{folio} " +
            "AND jw.source_name = #{source_name} " +
            "AND jw.source_id = #{source_id} " +
            "AND jw.flow_type = #{flow_type} " +
            "AND jw.project_id= #{project_id} ")
    int findRecordWorkOrder(WorkOrder workOrder);

    @Insert("INSERT INTO jira_workorder(feature, folio, board_id, project_id, source_id, source_name, flow_type, work_order_type, status_type, register_user_id, register_date) " +
            "VALUES (#{feature},#{folio},#{board_id}, #{project_id}, #{source_id},#{source_name}, #{flow_type}, #{work_order_type}, #{status_type}, #{register_user_id}, #{register_date})")
    @Options(useGeneratedKeys = true, keyProperty = "work_order_id", keyColumn = "work_order_id")
    void insertWorkOrder(WorkOrder workOrder);

    @Update("UPDATE jira_workorder " +
            "SET feature= #{feature}, folio=#{folio}, board_id=#{board_id}, project_id=#{project_id}, source_id=#{source_id}, "+
            "source_name=#{source_name}, flow_type=#{flow_type}, work_order_type=#{work_order_type}, status_type=#{status_type}, register_user_id=#{register_user_id}, register_date=#{register_date}, end_date=#{end_date} " +
            "WHERE work_order_id = #{work_order_id}")
    void UpdateWorkOrder(WorkOrder workOrder);

    @Insert({
            "<script>",
            "INSERT INTO jira_workorder_detail",
            "(work_order_id, template_id, issue_code, issue_status_type, register_user_id, register_date)",
            "VALUES" +
                    "<foreach item='element' collection='listWorkDetails' open='' separator=',' close=''>" +
                    "(" +
                    "#{element.work_order_id},",
                    "#{element.template_id},",
                    "#{element.issue_code},",
                    "#{element.issue_status_type},",
                    "#{element.register_user_id},",
                    "now()" +
                    ")" +
                    "</foreach>",
            "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "work_order_detail_id", keyColumn = "work_order_detail_id")
    void InsertDetailList(@Param("listWorkDetails")  List<WorkOrderDetail> listWorkDetails);

    @Select("CALL SP_WORK_ORDER_PAGED_FILTERED(#{pageCurrent},#{recordsAmount}, #{workOrderId} ,#{projectId})")
    List<WorkOrder> ListWorkOrder(@Param("pageCurrent") int pageCurrent,
                                  @Param("recordsAmount") int recordsAmount,
                                  @Param("workOrderId") int workOrderId,
                                  @Param("projectId") int projectId);

    @Select("CALL SP_WORK_ORDER_DETAIL_PAGED_FILTERED(#{pageCurrent},#{recordsAmount}, #{workOrderDetailId} ,#{workOrderId})")
    List<WorkOrderDetail> ListWorkOrderDetails(@Param("pageCurrent") int pageCurrent,
                                               @Param("recordsAmount") int recordsAmount,
                                               @Param("workOrderDetailId") int workOrderDetailId,
                                               @Param("workOrderId") int workOrderId);
}
