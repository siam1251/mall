/*
package com.kineticcafe.kcpmall.searchIndex;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class FormattedText extends Activity implements OnClickListener
{
	private int mFormatCounter;

	*/
/** Called when the activity is first created. *//*

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		findViewById(R.id.button).setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		TextView textView = (TextView) findViewById(R.id.text);

		CharSequence text = textView.getText();
//		<string name="text2">This @@text@@ has ##symbols## to designated $$where to format$$ using %%custom format spans%%.</string>

		switch (mFormatCounter++)
		{
		case 0:
			text = setSpanBetweenTokens(text, "##", new ForegroundColorSpan(0xFFFF0000));
			break;

		case 1:
			text = setSpanBetweenTokens(text, "$$", new StyleSpan(Typeface.BOLD_ITALIC));
			break;

		case 2:
			// Adapted from Linkify.addLinkMovementMethod(), to make links clickable.
			//
			MovementMethod m = textView.getMovementMethod();
			if ((m == null) || !(m instanceof LinkMovementMethod))
			{
				textView.setMovementMethod(LinkMovementMethod.getInstance());
			}

			text = setSpanBetweenTokens(text, "%%", new ForegroundColorSpan(0xFF4444FF), new UnderlineSpan(),
					new ClickableSpan()
			{
				@Override
				public void onClick(View widget)
				{
					// When the span is clicked, show some text on-screen.
					//
					findViewById(R.id.clicked_text).setVisibility(View.VISIBLE);
				}
			});
			break;

		case 3:
			text = setSpanBetweenTokens(text, "@@", new RelativeSizeSpan(1.5f), new ForegroundColorSpan(0xFFFFFFFF));

			// No more to do, so hide button
			//
			findViewById(R.id.button).setVisibility(View.GONE);
			break;
		}

		textView.setText(text);
	}

	*/
/**
	 * Given either a Spannable String or a regular String and a token, apply
	 * the given CharacterStyle to the span between the tokens, and also remove
	 * tokens.
	 * <p>
	 * For example, {@code setSpanBetweenTokens("Hello ##world##!", "##",
	 * new ForegroundColorSpan(0xFFFF0000));} will return a CharSequence {@code
	 * "Hello world!"} with {@code world} in red.
	 *
	 * @param text The text, with the tokens, to adjust.
	 * @param token The token string; there should be at least two instances of
	 *            token in text.
	 * @param cs The style to apply to the CharSequence. WARNING: You cannot
	 *            send the same two instances of this parameter, otherwise the
	 *            second call will remove the original span.
	 * @return A Spannable CharSequence with the new style applied.
	 *
	 * @see http://developer.android.com/reference/android/text/style/CharacterStyle.html
	 *//*

	public static CharSequence setSpanBetweenTokens(CharSequence text,
		String token, CharacterStyle... cs)
	{
		// Start and end refer to the points where the span will apply
		int tokenLen = token.length();
		int start = text.toString().indexOf(token) + tokenLen;
		int end = text.toString().indexOf(token, start);

		if (start > -1 && end > -1)
		{
			// Copy the spannable string to a mutable spannable string
			SpannableStringBuilder ssb = new SpannableStringBuilder(text);
			for (CharacterStyle c : cs)
				ssb.setSpan(c, start, end, 0);

			// Delete the tokens before and after the span
			ssb.delete(end, end + tokenLen);
			ssb.delete(start - tokenLen, start);

			text = ssb;
		}

		return text;
	}

}*/
