package org.auko.calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @packageName: calendar
 * @className: MyFocusListener
 * @Description: 自定义聚焦监听器
 * @author: auko
 * @data 2019-11-18 13:08
 */
public class MyFocusListener implements FocusListener {

    JButton btn;
    boolean isToday;
    static boolean previousIsToday = false;

    static List<JButton> clickHistory = new ArrayList<>();

    MyFocusListener(JButton btn) {
        this(btn, null);
    }

    MyFocusListener(JButton btn, Calendar calendar) {
        this.btn = btn;
        this.isToday = Util.isToday(calendar);
    }

    @Override
    public void focusGained(FocusEvent e) {
        btn.setBorderPainted(true);
        if(!(btn.getForeground() == Color.YELLOW)){
            // 只有不为黄色时才改成黑色, 黄色代表当天有toDo记录
            btn.setForeground(Color.BLACK);
        }
        btn.setContentAreaFilled(true);
        btn.setBackground(Color.LIGHT_GRAY);

        // 如果点击记录中有未恢复的日历元素, 则将其恢复回默认样式
        if (clickHistory.size() > 0){
            if(!previousIsToday){
                cleanBtnStyle(clickHistory.get(0));
            }else{
                clickHistory.get(0).setBackground(Color.WHITE);
            }
            clickHistory.clear();
            previousIsToday = false;
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        String aimBtnText = null;
        try {
            // 无视目标焦点为非button造成的类型强转异常或空指针异常
            aimBtnText = ((JButton) e.getOppositeComponent()).getText();
        } catch (ClassCastException ex) {
        } catch (NullPointerException ex) {
        }
        if (Util.isCalendarItem(aimBtnText)) {
            // 只有当失去焦点时, 目标是日历元素时才恢复回默认样式, 否则保持聚焦时的样式
            cleanBtnStyle(btn);
        } else {
            // 当失去焦点时点击了别的元素, 则记录此时日历元素, 方便之后再次点击日历时恢复回默认样式
            try {
                JButton previousBtn = (JButton) e.getComponent();
                clickHistory.add(previousBtn);
                if(clickHistory.size() == 1){
                    // 只记录第一个元素是否为今天
                    previousIsToday = isToday ? true : false;
                }
            } catch (ClassCastException ex) {
            }
        }
    }

    /**
     * 将按钮恢复为默认样式
     */
    private void cleanBtnStyle(JButton cleanBtn){
        if (!isToday) {
            cleanBtn.setBorderPainted(false);
            if(!(cleanBtn.getForeground() == Color.YELLOW)){
                // 当原来的样式不是黄色时, 才改颜色, 因为黄色代表当天有toDo记录
                if (cleanBtn.getFont().getSize() < 23) {
                    // 小号字说明不是本月
                    cleanBtn.setForeground(Color.LIGHT_GRAY);
                } else {
                    cleanBtn.setForeground(Color.WHITE);
                }
            }
            cleanBtn.setContentAreaFilled(false);
        } else {
            cleanBtn.setBackground(Color.WHITE);
        }
    }
}
