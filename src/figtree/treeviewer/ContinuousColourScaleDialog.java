package figtree.treeviewer;

import figtree.treeviewer.decorators.ContinuousScale;
import figtree.treeviewer.decorators.HSBContinuousColorDecorator;
import figtree.treeviewer.decorators.HSBDiscreteColorDecorator;
import figtree.ui.components.RangeSlider;
import jam.panels.OptionsPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * DiscreteColourScaleDialog.java
 *
 * @author			Andrew Rambaut
 * @version			$Id$
 */
public class ContinuousColourScaleDialog {
    private static final int SLIDER_RANGE = 1000;

    private JFrame frame;

    private HSBContinuousColorDecorator decorator;

    private JComboBox primaryAxisCombo = new JComboBox(HSBDiscreteColorDecorator.Axis.values());
    private SpinnerNumberModel secondaryCountSpinnerModel = new SpinnerNumberModel(2, 1, 100, 1);
    private JSpinner secondaryCountSpinner = new JSpinner(secondaryCountSpinnerModel);

    private RangeSlider hueSlider;
    private RangeSlider saturationSlider;
    private RangeSlider brightnessSlider;


    public ContinuousColourScaleDialog(final JFrame frame) {
        this.frame = frame;

        hueSlider = new RangeSlider(0, SLIDER_RANGE);
        saturationSlider = new RangeSlider(0, SLIDER_RANGE);
        brightnessSlider = new RangeSlider(0, SLIDER_RANGE);
    }

    public int showDialog() {

        final OptionsPanel options = new OptionsPanel(6, 6);

        final JComponent colourDisplay = new JComponent() {
            private final static int MAX_HEIGHT = 20;
            @Override
            public void paint(Graphics graphics) {
                Graphics2D g = (Graphics2D)graphics;
                Rectangle r = getBounds();
                int width = r.width;
                r.width = 1;
                ContinuousScale scale = decorator.getContinuousScale();
                double v = scale.getMinValue();
                double d = (scale.getMaxValue() - v) / width;
                for (int i = 0; i < width; i ++) {
                    g.setColor(decorator.getColour(v));
                    g.fill(r);
                    r.x ++;
                    v += d;

                }
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(super.getMaximumSize().width, MAX_HEIGHT);
            }

            @Override
            public Dimension getMinimumSize() {
                return new Dimension(super.getMinimumSize().width, MAX_HEIGHT);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, MAX_HEIGHT);
            }
        };
        options.addSpanningComponent(colourDisplay);

        options.addComponentWithLabel("Hue: ", hueSlider);
        options.addComponentWithLabel("Saturation: ", saturationSlider);
        options.addComponentWithLabel("Brightness: ", brightnessSlider);

        setDecorator(decorator);

        ChangeListener listener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setupDecorator(decorator);
                colourDisplay.repaint();
            }
        };

        hueSlider.addChangeListener(listener);
        saturationSlider.addChangeListener(listener);
        brightnessSlider.addChangeListener(listener);

        JOptionPane optionPane = new JOptionPane(options,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                null,
                null);
        optionPane.setBorder(new EmptyBorder(12, 12, 12, 12));

        final JDialog dialog = optionPane.createDialog(frame, "Setup colour range");
        dialog.pack();
        dialog.setResizable(true);
        dialog.setVisible(true);

        int result = JOptionPane.CANCEL_OPTION;
        Integer value = (Integer)optionPane.getValue();
        if (value != null && value.intValue() != -1) {
            result = value.intValue();
        }

        return result;
    }

    public void setDecorator(HSBContinuousColorDecorator decorator) {
        this.decorator = decorator;

        hueSlider.setValue((int)(decorator.getHueLower() * SLIDER_RANGE));
        hueSlider.setUpperValue((int) (decorator.getHueUpper() * SLIDER_RANGE));

        saturationSlider.setValue((int)(decorator.getSaturationLower() * SLIDER_RANGE));
        saturationSlider.setUpperValue((int)(decorator.getSaturationUpper() * SLIDER_RANGE));

        brightnessSlider.setValue((int)(decorator.getBrightnessLower() * SLIDER_RANGE));
        brightnessSlider.setUpperValue((int)(decorator.getBrightnessUpper() * SLIDER_RANGE));
    }

    public void setupDecorator(HSBContinuousColorDecorator decorator) {
        decorator.setHueLower(((float) hueSlider.getValue()) / SLIDER_RANGE);
        decorator.setHueUpper(((float) hueSlider.getUpperValue()) / SLIDER_RANGE);

        decorator.setSaturationLower(((float) saturationSlider.getValue()) / SLIDER_RANGE);
        decorator.setSaturationUpper(((float) saturationSlider.getUpperValue()) / SLIDER_RANGE);

        decorator.setBrightnessLower(((float) brightnessSlider.getValue()) / SLIDER_RANGE);
        decorator.setBrightnessUpper(((float) brightnessSlider.getUpperValue()) / SLIDER_RANGE);
    }

}