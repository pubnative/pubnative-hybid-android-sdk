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

package net.pubnative.lite.sdk.vast.processor;

import android.text.TextUtils;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vast.model.VASTMediaFile;
import net.pubnative.lite.sdk.vast.model.VASTModel;

import java.util.List;

public class VASTModelPostValidator {

    private static final String TAG = VASTModelPostValidator.class.getName();

    // This method tries to make sure that there is at least 1 Media file to
    // be used for VASTActivity. Also, if the boolean validateModel is true, it will
    // do additional validations which includes "at least 1 impression tracking url's is required'
    // If any of the above fails, it returns false. The false indicates that you can stop proceeding
    // further to display this on the MediaPlayer.

    public static boolean validate(VASTModel model, VASTMediaPicker mediaPicker) {

        Logger.d(TAG, "validate");

        if (!validateModel(model)) {

            Logger.d(TAG, "Validator returns: not valid (invalid model)");
            return false;
        }

        boolean isValid = false;

        // Must have a MediaPicker to choose one of the MediaFile element from XML
        if (mediaPicker != null) {

            List<VASTMediaFile> mediaFiles = model.getMediaFiles();
            VASTMediaFile mediaFile = mediaPicker.pickVideo(mediaFiles);

            if (mediaFile != null) {

                String url = mediaFile.getValue();

                if (!TextUtils.isEmpty(url)) {

                    isValid = true;
                    // Let's set this value inside VASTModel so that it can be
                    // accessed from VASTPlayer
                    model.setPickedMediaFileURL(url);
                    Logger.d(TAG, "mediaPicker selected mediaFile with URL " + url);
                }
            }

        } else {

            Logger.w(TAG, "mediaPicker: We don't have a compatible media file to play.");
        }

        Logger.d(TAG, "Validator returns: " + (isValid ? "valid" : "not valid (no media file)"));

        return isValid;
    }

    private static boolean validateModel(VASTModel model) {

        Logger.d(TAG, "validateModel");
        boolean isValid = true;

        // There should be at least one impression.
        List<String> impressions = model.getImpressions();

        if (impressions == null || impressions.size() == 0) {

            isValid = false;
        }

        // There must be at least one VASTMediaFile object
        List<VASTMediaFile> mediaFiles = model.getMediaFiles();

        if (mediaFiles == null || mediaFiles.size() == 0) {

            Logger.d(TAG, "Validator error: mediaFile list invalid");
            isValid = false;
        }

        return isValid;
    }
}
