//
// Copyright (c) 2016, PubNative, Nexage Inc.
// All rights reserved.
// Provided under BSD-3 license as follows:
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
// Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// Redistributions in binary form must reproduce the above copyright notice, this
// list of conditions and the following disclaimer in the documentation and/or
// other materials provided with the distribution.
//
// Neither the name of Nexage, PubNative nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
// ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

package net.pubnative.lite.sdk.vast;

import android.content.Context;
import android.os.AsyncTask;

import net.pubnative.lite.sdk.vast.model.VASTModel;
import net.pubnative.lite.sdk.vast.processor.VASTMediaPicker;
import net.pubnative.lite.sdk.vast.processor.VASTProcessor;
import net.pubnative.lite.sdk.vast.util.DefaultMediaPicker;

public class VASTParser extends AsyncTask<String, Object, VASTModel> {

    private static final String TAG = VASTParser.class.getName();

    public static final int ERROR_NONE = 0;
    public static final int ERROR_XML_OPEN_OR_READ = 1;
    public static final int ERROR_XML_PARSE = 2;
    public static final int ERROR_POST_VALIDATION = 3;
    public static final int ERROR_EXCEEDED_WRAPPER_LIMIT = 4;

    private Context context = null;
    private Listener listener = null;
    private int resultError = ERROR_NONE;

    public interface Listener {

        void onVASTParserError(int error);

        void onVASTParserFinished(VASTModel model);
    }

    public VASTParser(Context context) {

        this.context = context;
    }

    public VASTParser setListener(Listener listener) {

        this.listener = listener;
        return this;
    }

    @Override
    protected VASTModel doInBackground(String... params) {

        VASTModel result = null;
        this.resultError = ERROR_NONE;

        String vastXML = null;
        if (params.length > 0) {
            vastXML = params[0];
        }

        if (vastXML != null) {

            VASTMediaPicker mediaPicker = new DefaultMediaPicker(this.context);
            VASTProcessor processor = new VASTProcessor(mediaPicker);

            int error = processor.process(params[0]);

            if (error == ERROR_NONE) {

                result = processor.getModel();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(VASTModel result) {

        if (this.listener != null) {

            if (result == null) {

                this.listener.onVASTParserError(this.resultError);

            } else {

                this.listener.onVASTParserFinished(result);
            }
        }
    }
}
