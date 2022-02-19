<div>

    <div id="requestSection">
        <div>
            <p class="display-flex flex-column">
                <div class="col-md-12 no-padding">Upload an image Image below to get insights:</div>
                <!-- TEST -->
                <button style="display:block;width:120px; height:30px; margin-top: 40px; color: #000;"
                    onclick="document.getElementById('uploadImage').click()">Upload</button>
                <!-- <input type='file' id="uploadImage" style="display:none"> -->

                <input type="file" accept="image/*" id="uploadImage" name="files[]" size=40
                    onchange="handleFileSelect('uploadImage')" style="display: none;" />
            </p>

            <output id="thumbnail"></output>
        </div>

        <!-- <div>
            <p>Subscription key:
                <input type="text" id="key" name="subscription" size=40 maxlength="32" />
            </p>
        </div> -->

        <div style="display: none;">
            <p><a href="#" onclick="expandCollapse(options.id)">Query options</a></p>

            <div id="options" style="display:none">
                <p style="margin-left: 20px">Market:
                    <select id="mkt">
                        <option value="es-AR">Argentina (Spanish)</option>
                        <option value="en-AU">Australia (English)</option>
                        <option value="de-AT">Austria (German)</option>
                        <option value="nl-BE">Belgium (Dutch)</option>
                        <option value="fr-BE">Belgium (French)</option>
                        <option value="pt-BR">Brazil (Portuguese)</option>
                        <option value="en-CA">Canada (English)</option>
                        <option value="fr-CA">Canada (French)</option>
                        <option value="es-CL">Chile (Spanish)</option>
                        <option value="da-DK">Denmark (Danish)</option>
                        <option value="fi-FI">Finland (Finnish)</option>
                        <option value="fr-FR">France (French)</option>
                        <option value="de-DE">Germany (German)</option>
                        <option value="zh-HK">Hong Kong (Traditional Chinese)</option>
                        <option value="en-IN">India (English)</option>
                        <option value="en-ID">Indonesia (English)</option>
                        <option value="it-IT">Italy (Italian)</option>
                        <option value="ja-JP">Japan (Japanese)</option>
                        <option value="ko-KR">Korea (Korean)</option>
                        <option value="en-MY">Malaysia (English)</option>
                        <option value="es-MX">Mexico (Spanish)</option>
                        <option value="nl-NL">Netherlands (Dutch)</option>
                        <option value="en-NZ">New Zealand (English)</option>
                        <option value="no-NO">Norway (Norwegian)</option>
                        <option value="zh-CN">People's Republic of China (Chinese)</option>
                        <option value="pl-PL">Poland (Polish)</option>
                        <option value="pt-PT">Portugal (Portuguese)</option>
                        <option value="en-PH">Philippines (English)</option>
                        <option value="ru-RU">Russia (Russian)</option>
                        <option value="ar-SA">Saudi Arabia (Arabic)</option>
                        <option value="en-ZA">South Africa (English)</option>
                        <option value="es-ES">Spain (Spanish)</option>
                        <option value="sv-SE">Sweden (Swedish)</option>
                        <option value="fr-CH">Switzerland (French)</option>
                        <option value="de-CH">Switzerland (German)</option>
                        <option value="zh-TW">Taiwan (Traditional Chinese)</option>
                        <option value="tr-TR">Turkey (Turkish)</option>
                        <option value="en-GB">United Kingdom (English)</option>
                        <option value="en-US" selected>United States (English)</option>
                        <option value="es-US">United States (Spanish)</option>
                    </select>
                </p>
                <p style="margin-left: 20px">Safe search:
                    <select id="safesearch">
                        <option value="moderate" selected>Moderate</option>
                        <option value="strict">Strict</option>
                        <option value="off">off</option>
                    </select>
                </p>
            </div>
        </div>
    </div>

    <p><input type="button" id="query" value="Get insights"
            onclick="document.body.style.cursor='wait'; handleQuery()" /></p>

    <hr />

    <div id="responseSection"></div>
</div>