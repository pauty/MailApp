
package mailapp.client.view;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.CompoundBorder;
import mailapp.EMail;

/**
 *
 * @author pauty
 */
public class InboxMailRenderer extends JPanel implements ListCellRenderer<EMail>{
    private JLabel senderNameLabel;
    private JLabel subjectLabel;
    private JLabel dateLabel;
    
    public InboxMailRenderer() {

        senderNameLabel = new JLabel();
        subjectLabel = new JLabel();
        dateLabel = new JLabel();

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        
        senderNameLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,30));//top,left,bottom,right
        dateLabel.setBorder(BorderFactory.createEmptyBorder(0,30,0,0));
        senderNameLabel.setPreferredSize(new Dimension(180, 20));
        senderNameLabel.setMaximumSize(new Dimension(180, 20));
        this.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(1,0,1,0, Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(15,20,15,20)));
        
        add(senderNameLabel);
        add(subjectLabel);
        add(Box.createHorizontalGlue());
        add(dateLabel);
        
    }                      
                 
    @Override
    public Component getListCellRendererComponent(JList<? extends EMail> list, EMail mail, int i, boolean isSelected, boolean cellHasFocus) {
        
        senderNameLabel.setText(mail.getSender().getName());
        
        subjectLabel.setText(mail.getSubject());
        
        dateLabel.setText(mail.getDateString("dd/MM/yyyy    HH:mm:ss"));
        
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        return this;
    }
    
}
