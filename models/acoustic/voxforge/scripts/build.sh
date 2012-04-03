#!/bin/sh

download() {
cd tgz

wget -N -nd -c -e robots=off -A tgz,html -r -np \
 http://www.repository.voxforge1.org/downloads/SpeechCorpus/Trunk/Audio/Main/8kHz_16bit

cd ..
}

unpack() {
for f in tgz/*.tgz; do
    tar xf $f -C wav
done
}

convert_flac() {
find  wav -name "*flac*" -type d  | while read file; do
    outdir=${file//flac/wav}
    mkdir -p $outdir
done
find  wav -name "*.flac"  | while read f; do
    outfile=${f//flac/wav}
    flac -s -d $f -o $outfile
done
}

collect_prompts() {
mkdir etc
> etc/allprompts
find wav -name PROMPTS | while read f; do
    echo $f
    cat $f >> etc/allprompts
done
#find wav -name prompts | while read f; do
#    echo $f
#    cat $f >> etc/allprompts
#done
}

#FIXME
make_prompts() {
cat etc/allprompts | sort | sed 's/mfc/wav/g' |
sed 's:../../../Audio/MFCC/XXkHz_YYbit/MFCC_0_D/::g' > allprompts.tmp
mv allprompts.tmp etc/allprompts
cat etc/allprompts | awk '{
    printf ("<s> ");
    for (i=2;i<=NF;i++)
	printf ("%s ", $i);
    printf ("</s> (%s)\n", $1);
}
' > etc/voxforge_en_sphinx.transcription
./traintest.sh etc/voxforge_en_sphinx.transcription
./build_fileids.py etc/voxforge_en_sphinx.transcription.train > etc/voxforge_en_sphinx.fileids.train
./build_fileids.py etc/voxforge_en_sphinx.transcription.test > etc/voxforge_en_sphinx.fileids.test
}

collect_prompts
make_prompts
#./scripts_pl/make_feats.pl -ctl etc/voxforge_en_sphinx.fileids.train
