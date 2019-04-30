package com.anosi.asset.test;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.anosi.asset.model.jpa.DailyMaintenance;
import com.anosi.asset.model.jpa.Instrument;
import com.anosi.asset.model.jpa.SparePart;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * java类简单作用描述
 *
 * @ProjectName: goaland
 * @Package: com.anosi.asset.test
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/4/8 11:58
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/4/8 11:58
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public class TestEasyPoi {

    @Test
    public void testImportSparePart() {
        List<SparePart> spareParts = ExcelImportUtil.importExcel(new File("E:\\springWorkspace\\goaland\\src\\main\\resources\\static\\webResources\\templates\\sparePart.xlsx"), SparePart.class, new ImportParams());
        spareParts.forEach(System.out::println);
    }

    @Test
    public void testImportDaily() {
        List<DailyMaintenance> dailyMaintenances = ExcelImportUtil.importExcel(new File("E:\\springWorkspace\\goaland\\src\\main\\resources\\static\\webResources\\templates\\dailyMaintenance.xlsx"), DailyMaintenance.class, new ImportParams());
        dailyMaintenances.forEach(System.out::println);
    }

    @Test
    public void testImportInstrument() {
        List<Instrument> instruments = ExcelImportUtil.importExcel(new File("E:\\springWorkspace\\goaland\\src\\main\\resources\\static\\webResources\\templates\\instrument.xlsx"), Instrument.class, new ImportParams());
        instruments.forEach(System.out::println);
    }

}
