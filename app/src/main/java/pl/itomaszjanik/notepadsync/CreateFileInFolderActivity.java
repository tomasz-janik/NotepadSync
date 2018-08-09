/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.itomaszjanik.notepadsync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Task;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * An activity to illustrate how to query folders with title
 */

public class CreateFileInFolderActivity extends BaseDemoActivity {
    private static final String TAG = "CreateFileInFolder";
    private static String title = "",content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            title = bundle.getString("title", "");
            content = bundle.getString("content", "");
        }
    }

    @Override
    protected void onDriveClientReady() {
        getDriveClient().requestSync();
        queryFolder();
    }

    private Query setQueryFolder(){
        // [START query_title]
        return new Query.Builder()
                .addFilter(Filters.and(
                        Filters.eq(SearchableField.MIME_TYPE, "application/vnd.google-apps.folder"),
                        Filters.eq(SearchableField.TITLE, "NotepadSync"),
                        Filters.eq(SearchableField.TRASHED, false)
                ))
                .build();
        // [END query_title]
    }

    private void queryFolder() {
        final Query query = setQueryFolder();

        Task<MetadataBuffer> queryTask = getDriveResourceClient().query(query)
                .addOnSuccessListener(this, metadataBuffer -> {
                    if (metadataBuffer != null && metadataBuffer.getCount() > 0){
                        createFileInFolder(metadataBuffer.get(0).getDriveId().asDriveFolder());
                    }
                    else{
                        Intent intent = new Intent(this, CreateFolderActivity.class);
                        Bundle bundle = new Bundle();

                        bundle.putString("title", title);
                        bundle.putString("content", content);
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                    finish();
                })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Error retrieving files", e);
                    finish();
                });
    }

    private void createFileInFolder(final DriveFolder parent) {
        getDriveResourceClient().createContents()
                .continueWithTask(task -> {
                    DriveContents contents = task.getResult();
                    OutputStream outputStream = contents.getOutputStream();
                    try (Writer writer = new OutputStreamWriter(outputStream)) {
                        writer.write(content);
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(title + ".txt")
                            .setMimeType("text/plain")
                            .setStarred(true)
                            .build();

                    return getDriveResourceClient().createFile(parent, changeSet, contents);
                })
                .addOnFailureListener(this, e ->
                    Log.e(TAG, "Unable to create file", e)
                );
    }
}
