<template>
  <div>
    <div class="text_wrap">
      <input
        type="text"
        name="title"
        v-model="title"
        class="page_tit"
        placeholder="제목을 입력하세요."
      />
      <br />
      <input
        v-if="params.now3 == ''"
        type="text"
        name="regId"
        v-model="regId"
        class="author"
        placeholder="작성자를 입력하세요."
      />
      <input
        v-else
        type="text"
        name="modId"
        v-model="modId"
        class="author"
        placeholder="수정한 사람을 입력하세요."
      />
    </div>
    <div id="editor"></div>
    <div class="in_box dl_input_box line">
      <dl class="line">
        <dd>
          <div class="file_input d_inblock">
            <label class="btn_file wid150">
              <span class="left icon_file">파일첨부</span>
              <input
                type="file"
                ref="uploadFile"
                @change="uploadFile"
                multiple
              />
            </label>
          </div>
          <ul class="file_add_list">
            <li v-for="(item, index) in uploadedFile" v-bind:key="index">
              <span class="name"
                ><a :href="'/file/download.do?seq=' + item.file_seq">{{
                  item.file_name
                }}</a></span
              >
              <a
                href="javascript:void(0)"
                @click="deleteFile(item.file_seq, index)"
                class="btn_del"
                >삭제</a
              >
            </li>
          </ul>
        </dd>
      </dl>
    </div>
    <div class="btn_wrap">
      <a href="javascript:void(0)" class="btn01 col02" @click="save">저장</a>
      <a href="javascript:void(0)" class="btn01 col04" @click="cancel">취소</a>
    </div>
  </div>
</template>
<script>
module.exports = {
  props: ["callback", "params", "info"],
  data: function () {
    return {
      title: this.info.title,
      contents: this.info.contents,
      regDttm: this.info.reg_dttm,
      regId: this.info.reg_id,
      modDttm: this.info.mod_dttm,
      modId: this.info.mod_id,
      editor: "",
      uploadedFile: [],
      fileMasterSeq: this.info.file_master_seq,
    };
  },
  mounted: function () {
    console.log("mount");
    const editor = new toastui.Editor({
      el: document.querySelector("#editor"),
      height: "900px",
      initialValue: this.contents,
      initialEditType: "wysiwyg",
    });
    this.editor = editor;
    this.getFileList();
  },
  created: function () {},
  distroyed: function () {},
  methods: {
    save: function () {
      let vm = this;
      comm.post(
        {
          url: "./data/addContents",
          params: {
            upSeq: vm.params.now2,
            seq: vm.params.now3,
            title: vm.title,
            contents: this.editor.getMarkdown(),
            fileMasterSeq: vm.fileMasterSeq,
            regDttm: vm.regDttm,
            regId: vm.regId,
            modDttm: vm.modDttm,
            modId: vm.modId,
          },
        },
        function (data) {
          alert("저장되었습니다.");
          location.reload();
        }
      );
    },
    cancel: function () {
      this.$emit("cancel");
    },
    uploadFile: function (event) {
      let vm = this;
      let files = event.target.files;
      console.log(files);
      if (files.length == 0) {
        alert("파일을 선택하여 주십시오.");
        return false;
      }
      var fileFormData = new FormData();
      for (let item of files) {
        fileFormData.append("file", item);
      }
      if (vm.fileMasterSeq > 0) {
        fileFormData.append("fileMasterSeq", vm.fileMasterSeq);
      }

      comm.post(
        {
          url: "/file/upload.do",
          processData: false,
          contentType: false,
          headers: {
            "Content-Type": "multipart/form-data",
          },
          params: fileFormData,
        },
        function (data) {
          console.log("🚀 ~ file: toastUiEditor.vue ~ line 147 ~ data", data);
          if (data.result.length < 1) {
            return false;
          } else {
            if (vm.uploadedFile.length > 0) {
              if (data.result.uploadFileList.length > 0) {
                for (let item of data.result.uploadFileList) {
                  if (item.status == "fail") {
                    alert(item.message);
                  } else {
                    vm.uploadedFile.push(item.fileInfo);
                  }
                }
              } else {
                // no file
              }
            } else {
              if (data.result.uploadFileList.length > 0) {
                for (let item of data.result.uploadFileList) {
                  if (item.status == "fail") {
                    alert(item.message);
                  } else {
                    vm.uploadedFile.push(item.fileInfo);
                  }
                }
              } else {
                // no file
              }
            }
            vm.fileMasterSeq = data.result.fileMasterSeq;
          }
        }
      );

      vm.$refs.uploadFile.value = null;
    },
    deleteFile: function (seq, index) {
      var fileSeq = seq;
      let vm = this;
      comm.post(
        {
          url: "/file/delete.do",
          params: {
            seq: fileSeq,
          },
        },
        function (data) {
          vm.uploadedFile.splice(index, 1);
        }
      );
    },
    getFileList: function () {
      let vm = this;
      // 첨부파일 가져오기
      comm.post(
        {
          url: "/data/getFileList",
          params: {
            fileMasterSeq: vm.fileMasterSeq,
          },
        },
        function (data) {
          if (data.result.length < 1) {
            return false;
          } else {
            vm.uploadedFile = data.result;
            console.log(
              "🚀 ~ file: toastUiEditor.vue ~ line 190 ~ vm.uploadedFile",
              vm.uploadedFile
            );
          }
        }
      );
    },
  },
};
</script>
