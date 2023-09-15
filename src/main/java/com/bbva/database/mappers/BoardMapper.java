package com.bbva.database.mappers;

import com.bbva.entities.board.Board;
import com.bbva.entities.board.BoardListEntity;
import com.bbva.entities.board.BoardPaginationEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BoardMapper {

    @Select("SELECT * FROM jira_board")
    List<Board> list();

    @Select("SELECT * FROM jira_board where board_id=#{boardId}")
    Board boardById(@Param("boardId") Integer boardId);

    @Select("CALL SP_BOARD_PAGED_FILTERED(" +
            "#{page}," +
            "#{records_amount}," +
            "#{sourceId}," +
            "#{fuenteOrigen}," +
            "#{fuenteDatio}," +
            "#{functionalDescription})")
    List<BoardPaginationEntity> pagination(@Param("page") Integer page,
                                           @Param("records_amount") Integer records_amount,
                                           @Param("sourceId") Number sourceId,
                                           @Param("fuenteOrigen") String fuenteOrigen,
                                           @Param("fuenteDatio") String fuenteDatio,
                                           @Param("functionalDescription") String functionalDescription);

    @Select("CALL SP_BOARD_FILTERED_BY_ID(#{id})")
    List<BoardListEntity> lista(@Param("id") Integer id);
}
