/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tom_roush.pdfbox.contentstream.operator.markedcontent;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import com.tom_roush.pdfbox.contentstream.operator.MissingOperandException;
import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.contentstream.operator.OperatorName;
import com.tom_roush.pdfbox.contentstream.operator.OperatorProcessor;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.graphics.PDXObject;
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDFormXObject;
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import com.tom_roush.pdfbox.text.PDFMarkedContentExtractor;

/**
 * Do: Draws an XObject.
 *
 * @author Ben Litchfield
 * @author Mario Ivankovits
 */
public class DrawObject extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        if (arguments.isEmpty())
        {
            throw new MissingOperandException(operator, arguments);
        }
        COSBase base0 = arguments.get(0);
        if (!(base0 instanceof COSName))
        {
            return;
        }
        COSName name = (COSName) base0;
        PDXObject xobject = context.getResources().getXObject(name);
        ((PDFMarkedContentExtractor) context).xobject(xobject);

        if (xobject instanceof PDFormXObject)
        {
            try
            {
                context.increaseLevel();
                if (context.getLevel() > 25)
                {
                    Log.e("PdfBox-Android", "recursion is too deep, skipping form XObject");
                    return;
                }
                PDFormXObject form = (PDFormXObject) xobject;
                if (form instanceof PDTransparencyGroup)
                {
                    context.showTransparencyGroup((PDTransparencyGroup) form);
                }
                else
                {
                    context.showForm(form);
                }
            }
            finally
            {
                context.decreaseLevel();
            }
        }
    }

    @Override
    public String getName()
    {
        return OperatorName.DRAW_OBJECT;
    }
}
