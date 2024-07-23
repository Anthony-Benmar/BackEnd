package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.BoardMapper;
import com.bbva.database.mappers.CatalogMapper;
import com.bbva.database.mappers.IssueTicketMapper;
import com.bbva.database.mappers.TemplateMapper;
import com.bbva.dto.issueticket.request.sourceTicketDtoRequest;
import com.bbva.dto.issueticket.response.*;
import com.bbva.dto.jira.request.*;
import com.bbva.entities.board.Board;
import com.bbva.entities.common.CatalogEntity;
import com.bbva.entities.feature.JiraFeatureEntity;
import com.bbva.entities.issueticket.WorkOrder;
import com.bbva.entities.issueticket.WorkOrderDetail;
import com.bbva.entities.template.Template;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class IssueTicketDao {

    private static final Logger LOGGER = Logger.getLogger(IssueTicketDao.class.getName());

    private static IssueTicketDao instance = null;

    public static synchronized IssueTicketDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new IssueTicketDao();
        }

        return instance;
    }

    public int findRecordWorkOrder(WorkOrder item) {
        int result = 0;
        try{
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                IssueTicketMapper issueMapper = session.getMapper(IssueTicketMapper.class);
                result = issueMapper.findRecordWorkOrder(item);
                return result;
            }
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return result;
        }
    }

    public void insertWorkOrderAndDetail(WorkOrder workorder, List<WorkOrderDetail> workerDetails) {
        try{
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                IssueTicketMapper issueMapper = session.getMapper(IssueTicketMapper.class);
                try{
                    issueMapper.insertWorkOrder(workorder);
                    workerDetails.forEach(wod->wod.setWork_order_id(workorder.work_order_id));
                    issueMapper.InsertDetailList(workerDetails);
                    session.commit();
                }catch (Exception e){
                    session.rollback();
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public boolean UpdateWorkOrder(WorkOrder item) {
        try{
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                IssueTicketMapper issueMapper = session.getMapper(IssueTicketMapper.class);
                issueMapper.UpdateWorkOrder(item);
                session.commit();
                return true;
            }
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    public boolean InsertWorkOrderDetail(List<WorkOrderDetail> item) {
        try{
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                IssueTicketMapper issueMapper = session.getMapper(IssueTicketMapper.class);
                issueMapper.InsertDetailList(item);
                session.commit();
                return true;
            }
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }
    public boolean InsertWorkOrderDetail(SqlSession session, List<WorkOrderDetail> item) {
        try{
            IssueTicketMapper issueMapper = session.getMapper(IssueTicketMapper.class);
            issueMapper.InsertDetailList(item);
            return true;
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    public List<WorkOrder> ListWorkOrder(int workOrderId) {

        List<WorkOrder> result =null;
        try{
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                IssueTicketMapper issueMapper = session.getMapper(IssueTicketMapper.class);
                result = issueMapper.ListWorkOrder(1,0,workOrderId,0);
            }
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return  result;
    }

    public List<WorkOrderDetail> ListWorkOrderDetails(int workOrderId) {
        List<WorkOrderDetail> result = null;
        try{
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                IssueTicketMapper issueMapper = session.getMapper(IssueTicketMapper.class);
                result = issueMapper.ListWorkOrderDetails(1,0,0,workOrderId);
            }
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }



    public Map<Integer, IssueDto> getDataRequestIssueJira(WorkOrder workOrder, List<WorkOrderDetail> workOrderDetail)
    {
        var result = new HashMap<Integer, IssueDto>();
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        Board board = new Board();
        List<Template> templates = new ArrayList<Template>();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BoardMapper boardMapper = session.getMapper(BoardMapper.class);
            List<Board> boards = boardMapper.list();
            board = boards.stream().filter(b->b.board_id.equals(workOrder.board_id)).findFirst().orElse(null);

            TemplateMapper templateMapper = session.getMapper(TemplateMapper.class);
            templates = templateMapper.list();

            Set<Integer> acceptableTemplate_id = workOrderDetail.stream().map(w->w.template_id)
                    .collect(Collectors.toSet());

            templates = templates.stream().filter(t-> acceptableTemplate_id.contains(t.template_id))
                    .collect(Collectors.toList());
        }


        for (WorkOrderDetail item:workOrderDetail)
        {
            var template = templates.stream().filter(t -> t.template_id.equals(item.template_id))
                    .findFirst().orElse(null);

            var issueJira = new IssueDto();
            issueJira.fields = new Fields();
            issueJira.fields.project = new Project();
            issueJira.fields.project.id = board.project_jira_id;
            issueJira.fields.project.key = board.project_jira_key;

            Board finalBoard = board;
            issueJira.fields.customfield_13300 = new ArrayList<String>(){{
                add(finalBoard.board_jira_id);
            }};
            issueJira.fields.customfield_10270 = new Customfield();
            issueJira.fields.customfield_10270.value="Technical";
            issueJira.fields.customfield_10270.id="20247";
            issueJira.fields.customfield_10270.disabled= false;
            issueJira.fields.labels = new ArrayList<String>() {{
                add(String.format("P-%s", template.label_one));
                add(String.format("F-%s", workOrder.folio));
                add(String.format("ID-%s", workOrder.source_id));
            }};

            issueJira.fields.customfield_10004 = workOrder.feature;
            issueJira.fields.summary = template.name.replace("[fuente]", workOrder.source_name);
            issueJira.fields.description = template.description;

            issueJira.fields.issuetype = new Issuetype();
            issueJira.fields.issuetype.name = "Story";

            result.put(item.template_id, issueJira);
        }

        return result;
    }

    public IssueBulkDto getDataRequestIssueJira2(WorkOrder workOrder, List<WorkOrderDetail> workOrderDetail, JiraFeatureEntity feature)
    {
        var result = new IssueBulkDto();
        result.issueUpdates = new ArrayList<IssueUpdate>();

        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        Board board = null;
        List<Template> templates = new ArrayList<Template>();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BoardMapper boardMapper = session.getMapper(BoardMapper.class);
            board = boardMapper.boardById(workOrder.board_id);
            var acceptableTemplate_id = workOrderDetail.stream().map(w->w.template_id)
                    .collect(Collectors.toList());

            TemplateMapper templateMapper = session.getMapper(TemplateMapper.class);
            templates = templateMapper.listById(acceptableTemplate_id);
        }

        for (WorkOrderDetail item:workOrderDetail)
        {
            IssueUpdate issueUpdate = new IssueUpdate();
            var template = templates.stream().filter(t -> t.template_id.equals(item.template_id))
                    .findFirst().orElse(null);

            var fields = new Fields();
            fields.project = new Project();
            fields.project.id = feature.jiraProjectId.toString();
            fields.project.key = feature.jiraProjectName;

            Board finalBoard = board;
            fields.customfield_13300 = new ArrayList<String>(){{
                add(finalBoard.board_jira_id);
            }};
            fields.customfield_10270 = new Customfield();
            fields.customfield_10270.value="Technical";
            fields.customfield_10270.id="20247";
            fields.customfield_10270.disabled= false;
            fields.labels = new ArrayList<String>() {{
                add(String.format("P-%s", template.label_one));
                add(String.format("F-%s", workOrder.folio));
                add(String.format("ID-%s", workOrder.source_id));
            }};

            fields.customfield_10004 = workOrder.feature;
            fields.summary = template.name.toLowerCase().replace("[fuente]", workOrder.source_name);
            fields.description = template.description;

            fields.issuetype = new Issuetype();
            fields.issuetype.name = "Story";

            issueUpdate.fields = fields;
            result.issueUpdates.add(issueUpdate);
        }

        return result;
    }

    public Map<String, IssueDto> getDataRequestIssueJiraEdit(WorkOrder workOrder, List<WorkOrderDetail> workOrderDetail, JiraFeatureEntity feature)
    {
        var result = new HashMap<String, IssueDto>();
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        Board board = new Board();
        List<Template> templates = new ArrayList<Template>();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BoardMapper boardMapper = session.getMapper(BoardMapper.class);
            List<Board> boards = boardMapper.list();
            board = boards.stream().filter(b->b.board_id.equals(workOrder.board_id)).findFirst().orElse(null);

            TemplateMapper templateMapper = session.getMapper(TemplateMapper.class);
            templates = templateMapper.list();

            Set<Integer> acceptableTemplate_id = workOrderDetail.stream().map(w->w.template_id)
                    .collect(Collectors.toSet());

            templates = templates.stream().filter(t-> acceptableTemplate_id.contains(t.template_id))
                    .collect(Collectors.toList());
        }


        for (WorkOrderDetail item:workOrderDetail)
        {
            var template = templates.stream().filter(t -> t.template_id.equals(item.template_id))
                    .findFirst().orElse(null);

            var issueJira = new IssueDto();
            issueJira.fields = new Fields();
            issueJira.fields.project = new Project();
            issueJira.fields.project.id = feature.jiraProjectId.toString();
            issueJira.fields.project.key = feature.jiraProjectName;

            Board finalBoard = board;
            issueJira.fields.customfield_13300 = new ArrayList<String>(){{
                add(finalBoard.board_jira_id);
            }};
            issueJira.fields.customfield_10270 = new Customfield();
            issueJira.fields.customfield_10270.value="Technical";
            issueJira.fields.customfield_10270.id="20247";
            issueJira.fields.customfield_10270.disabled= false;
            issueJira.fields.labels = new ArrayList<String>() {{
                add(String.format("P-%s", template.label_one));
                add(String.format("F-%s", workOrder.folio));
                add(String.format("ID-%s", workOrder.source_id));
            }};

            issueJira.fields.customfield_10004 = workOrder.feature;
            issueJira.fields.summary = template.name.toLowerCase().replace("[fuente]", workOrder.source_name);
            issueJira.fields.description = template.description;

            issueJira.fields.issuetype = new Issuetype();
            issueJira.fields.issuetype.name = "Story";

            result.put(item.issue_code, issueJira);
        }

        return result;
    }


    public sourceTicketDtoResponse listSources(sourceTicketDtoRequest dto)
    {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<Board> boards = null;
        List<WorkOrder> workOrders = null;
        List<CatalogEntity> flowTypes = null;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BoardMapper boardMapper = session.getMapper(BoardMapper.class);
            CatalogMapper catalogMapper = session.getMapper(CatalogMapper.class);
            boards = boardMapper.list();
            IssueTicketMapper issueTicketMapper = session.getMapper(IssueTicketMapper.class);
            workOrders = issueTicketMapper.ListWorkOrder(dto.page,0,0,dto.projectId);
            var filterCatalog = new int[]{ 1023 };
            flowTypes = catalogMapper.getListByCatalog(filterCatalog);
        }

        List<Board> finalBoards = boards;
        List<CatalogEntity> finalFlowTypes = flowTypes;
        var listWorkOrderResult = workOrders.stream().map(wo->{
            var board = finalBoards.stream().filter(b -> b.board_id.equals(wo.board_id))
                    .findFirst().orElse(new Board());
            var catalogTypeFlow =  finalFlowTypes.stream().filter(c->c.getElementId().equals(wo.flow_type))
                    .findFirst().orElse(null);
            String typeDesc = catalogTypeFlow != null ? catalogTypeFlow.getElementName() : "";
            return new issueTicketDetailDtoResponse(wo.work_order_id,0,0,"", wo.board_id
                    ,wo.flow_type, typeDesc, board.name, wo.source_id, null, "", wo.folio, wo.source_name,
                    wo.feature, "",wo.project_id,0,wo.register_date);
        }).collect(Collectors.toList());


        var groupWorkOrder  = listWorkOrderResult.stream()
                .collect(Collectors.groupingBy(register ->
                                Arrays.asList(register.folio_code,
                                        register.old_source_id,
                                        register.source,
                                        Integer.toString(register.boardId),
                                        register.boardName,
                                        register.feature
                                )
                ));

        var result = groupWorkOrder.entrySet().stream()
                .map(w -> {
                    var keys= w.getKey();
                    var sourceDetailList = w.getValue().stream()
                            .map(d-> new sourceTicketDetailDtoResponse(d.workOrderId,d.typeId,d.typeDesc))
                            .collect(Collectors.toList());
                    var maxRegisterDate = w.getValue().stream()
                            .map(issueTicketDetailDtoResponse::getRegister_date)
                            .max(Comparator.naturalOrder()).stream()
                            .findFirst().orElse(null);
                    return new sourceTicketGroupByDtoResponse(keys.get(0), keys.get(1),keys.get(2), Integer.parseInt(keys.get(3)),keys.get(4), keys.get(5), maxRegisterDate, sourceDetailList);
                }).collect(Collectors.toList());

        Integer count =  (int)result.stream().count();
        var pages_amount = dto.getRecords_amount()>0 ? (int)Math.ceil(count.floatValue() / dto.getRecords_amount().floatValue()):1;

        result = result.stream().sorted(Comparator.comparing(sourceTicketGroupByDtoResponse::getRegisterDate).reversed())
                .collect(Collectors.toList());

        if(dto.records_amount>0){
            result = result.stream()
                    .skip(dto.records_amount * (dto.page - 1))
                    .limit(dto.records_amount)
                    .collect(Collectors.toList());
        }

        return new sourceTicketDtoResponse(count.intValue(), pages_amount, result);

    }

    public issueTicketDtoResponse listIssuesGenerated(sourceTicketDtoRequest dto)
    {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<Template> templates = null;
        WorkOrder workOrder = null;
        List<WorkOrderDetail> workOrderDetails = null;

        try (SqlSession session = sqlSessionFactory.openSession()) {
            TemplateMapper templatedMapper = session.getMapper(TemplateMapper.class);
            templates = templatedMapper.list();
            templates = templates.stream().filter(t->t.type_id.equals(dto.type) && t.status.equals(1)).collect(Collectors.toList());
            IssueTicketMapper issueTicketMapper = session.getMapper(IssueTicketMapper.class);
            workOrder = issueTicketMapper.ListWorkOrder(1,0,dto.workOrderId,0).stream().findFirst().orElse(null);
            workOrderDetails = issueTicketMapper.ListWorkOrderDetails(1,0,0,dto.workOrderId);
        }

        var groupWorkOrderDetail  = workOrderDetails.stream()
                .collect(Collectors.groupingBy(g-> g.template_id));

        var result_templates = templates.stream().map( t -> {
            return new issueTicketDetailDtoResponse(0,0, t.template_id, t.name,0,0, "","","",null,t.label_one,"","","","",0,t.orden,null);
        }).collect(Collectors.toList());

        Set<Integer> acceptableTemplate_id = templates.stream().map(w->w.template_id)
                .collect(Collectors.toSet());

        List<Template> finalTemplates = templates;
        WorkOrder finalWorkOrder = workOrder;

        List<issueTicketDetailDtoResponse> result = groupWorkOrderDetail.entrySet().stream()
                .filter(f-> acceptableTemplate_id.contains(f.getKey()))
                .map(w -> {
                    var tmp = finalTemplates.stream().filter(t->t.template_id.equals(w.getKey())).findFirst().orElse(null);
                    var wodMax = w.getValue().stream().max(Comparator.comparing(WorkOrderDetail::getWork_order_detail_id)).get();
                    var issues = w.getValue().stream()
                            .map(n-> new issueJiraDtoResponse(n.issue_code,n.issue_status_type))
                            .collect(Collectors.toList());
                    return new issueTicketDetailDtoResponse(finalWorkOrder.work_order_id,wodMax.work_order_detail_id, wodMax.template_id, tmp.name, finalWorkOrder.board_id,0, "","","",
                            issues,tmp.label_one,"","","","",0, tmp.orden,null);
                }).collect(Collectors.toList());

        Set<Integer> noacceptableTemplate_id = result.stream().map(w->w.templateId)
                .collect(Collectors.toSet());

        var extendResult = result_templates.stream()
                .filter(t -> !noacceptableTemplate_id.contains(t.templateId))
                .collect(Collectors.toList());

        result.addAll(extendResult);

        Long count = result.stream().count();
        var pages_amount = dto.getRecords_amount()>0? (int)Math.ceil(count.floatValue() / dto.getRecords_amount().floatValue()) : 1;

        result = result.stream().sorted(Comparator.comparing(issueTicketDetailDtoResponse::getOrderTemplate))
                .collect(Collectors.toList());


        if(dto.records_amount>0){
            result = result.stream()
                    .skip(dto.records_amount * (dto.page - 1))
                    .limit(dto.records_amount)
                    .collect(Collectors.toList());
        }

        return new issueTicketDtoResponse(count.intValue(), pages_amount, result);

    }
}