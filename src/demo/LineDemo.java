package demo;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class LineDemo {

	public static void main(String[] args) {
		//构造数据集合
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(12, "中国", "北京");
		dataset.addValue(6, "中国", "上海");
		dataset.addValue(2, "中国", "深圳");
		
		dataset.addValue(10, "中国1", "北京");
		dataset.addValue(3, "中国1", "上海");
		dataset.addValue(1, "中国1", "深圳");
		
		dataset.addValue(9, "中国2", "北京");
		dataset.addValue(5, "中国2", "上海");
		dataset.addValue(1, "中国2", "深圳");
		
//		dataset.addValue(10, "美国", "西雅图");
//		dataset.addValue(6, "美国", "纽约");
//		dataset.addValue(2, "美国", "华盛顿");
		
		JFreeChart chart = ChartFactory.createLineChart(
							"用户统计报表（所属单位）",		//图形的主标题 
							"所属单位名称", 			//种类轴的标签
							"数量", 					//值轴的标签
							dataset, 				//图形的数据集合
							PlotOrientation.VERTICAL,//图表的显示形式（水平和垂直） 			
							true,					//是否显示子标题 
							true,					//是否生成数据提示 
							true					//是否生成URL链接
		);
		//处理主标题乱码
		chart.getTitle().setFont(new Font("宋体", Font.BOLD, 18));
		//处理子标题乱码
		chart.getLegend().setItemFont(new Font("宋体", Font.BOLD, 15));
		//获取图表区域对象
		/**
		 * 3种方式获取对象
		 *   * 方式一：断点
		 *   * 方式二：使用System.out.println();
		 *   * 方案三：使用API
		 */
		CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
		//获取X轴对象
		CategoryAxis categoryAxis = (CategoryAxis) categoryPlot.getDomainAxis();
		//获取Y轴对象
		NumberAxis numberAxis = (NumberAxis) categoryPlot.getRangeAxis();
		//处理X轴上的乱码
		categoryAxis.setTickLabelFont(new Font("宋体", Font.BOLD, 15));
		//处理X轴外的乱码
		categoryAxis.setLabelFont(new Font("宋体", Font.BOLD, 15));
		//处理Y轴上的乱码
		numberAxis.setTickLabelFont(new Font("宋体", Font.BOLD, 15));
		//处理Y轴外的乱码
		numberAxis.setLabelFont(new Font("宋体", Font.BOLD, 15));
		
		//设置刻度以1为单位
		numberAxis.setAutoTickUnitSelection(false);//手动设置
		NumberTickUnit unit = new NumberTickUnit(1);//以1为单位
		numberAxis.setTickUnit(unit);
		
		//获取绘图区域对象
		LineAndShapeRenderer lineAndShapeRenderer = (LineAndShapeRenderer) categoryPlot.getRenderer();

		//在图形上生成数字
		lineAndShapeRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		lineAndShapeRenderer.setBaseItemLabelsVisible(true);
		lineAndShapeRenderer.setBaseItemLabelFont(new Font("宋体", Font.BOLD, 15));
		
		//设置转折点（以正方形作为转折点）
		Shape shape = new Rectangle(10,10);
		lineAndShapeRenderer.setSeriesShape(0, shape);//参数一int类型表示第几条线，默认是0,0表示第一条线
		lineAndShapeRenderer.setSeriesShapesVisible(0, true);
		
		lineAndShapeRenderer.setSeriesShape(1, shape);//参数一int类型表示第几条线，默认是0,0表示第一条线
		lineAndShapeRenderer.setSeriesShapesVisible(1, true);
		
		lineAndShapeRenderer.setSeriesShape(2, shape);//参数一int类型表示第几条线，默认是0,0表示第一条线
		lineAndShapeRenderer.setSeriesShapesVisible(2, true);
		
		//使用Rrame加载图形
		ChartFrame chartFrame = new ChartFrame("xyz", chart);
		chartFrame.setVisible(true);
		//输出图形
		chartFrame.pack();
	}
}
