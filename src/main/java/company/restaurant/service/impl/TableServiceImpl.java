package company.restaurant.service.impl;

import company.restaurant.service.TableService;
import company.restaurant.entity.Table;
import company.restaurant.mapper.TableMapper;
import company.restaurant.vo.TableListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;


//查询桌号的实现类
@Service
@Slf4j
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {
    private final TableMapper tableMapper;
    @Override
    public List<TableListVO> getTableList() {
        //查询所有的桌子
        List<Table> tableList = tableMapper.selectList(null);
        return tableList.stream()
                .map(table -> {
                    //桌位显示名称
                    String displayName="";
                    String tableTypeText="";
                    if (table.getTableType()==0) {
                        displayName="桌号"+table.getId();
                        tableTypeText="大厅";
                    }else if (table.getTableType()==1) {
                        displayName=table.getTableName();
                        tableTypeText="包间";

                    }
                    return TableListVO.builder()
                            .id(table.getId())
                            .tableName(displayName)
                            .tableType(table.getTableType())
                            .tableTypeText(tableTypeText)
                            .build();
                }).toList();
    }
}
