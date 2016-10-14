
package me.ferrybig.javacoding.neuralnetwork.gui;

import java.text.ParseException;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;

public class DoubleFormatterFactory extends AbstractFormatterFactory {

	public static final DoubleFormatterFactory INSTANCE = new DoubleFormatterFactory();
	public static final DoubleFormatter INSTANCE_FORMATTER = new DoubleFormatter();

	@Override
	public DoubleFormatter getFormatter(JFormattedTextField tf) {
		return INSTANCE_FORMATTER;
	}

	public static class DoubleFormatter extends AbstractFormatter {

		public DoubleFormatter() {
		}

		@Override
		public Object stringToValue(String text) throws ParseException {
			try {
				return Double.valueOf(text);
			} catch (NumberFormatException e) {
				throw (ParseException)(new ParseException(e.toString(), 0).initCause(e));
			}
		}

		@Override
		public String valueToString(Object value) {
			return String.format(Locale.ENGLISH, "%+.11g", value);
		}
	}

}
