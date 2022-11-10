package com.david.notify;


import static com.badlogic.gdx.graphics.GL20.GL_BACK;
import static com.badlogic.gdx.graphics.GL20.GL_LEQUAL;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.model.Animation;

import com.badlogic.gdx.utils.GdxRuntimeException;

import com.badlogic.gdx.utils.JsonReader;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.FileHandler;

import javax.imageio.ImageIO;


public class DavidRenderer extends ApplicationAdapter
{
    /*SpriteBatch renderer;
    Texture david;

    Rectangle rect;
    float x;
    float y;

    @Override
    public void create()
    {
        renderer = new SpriteBatch();
        david = new Texture(Gdx.files.internal("david.png"));

        rect = new Rectangle
        (
            (Gdx.graphics.getWidth() / 2) - david.getWidth() * 2, (Gdx.graphics.getHeight() / 2) - david.getHeight() * 2, david.getWidth() * 2, david.getHeight() * 2
        );



    }

    @Override
    public void render ()
    {
        //Clearing the background
        Gdx.gl.glClearColor(0.5f, 0.5f, 1, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.begin();

        renderer.draw(
                david, rect.x, rect.y, rect.width, rect.height
                );

        renderer.end();


    }

    @Override
    public void dispose ()
    {
        renderer.dispose();
    } */


    public class MYshader implements Shader {

        ShaderProgram program;
        Camera camera;
        RenderContext ctx;

        @Override
        public void init()
        {
            String vertex = Gdx.files.internal("Shaders/vertex.glsl").readString();
            String fragment = Gdx.files.internal("Shaders/fragment.glsl").readString();
            program = new ShaderProgram(vertex, fragment);

            if (!program.isCompiled())
            {
                throw new GdxRuntimeException(program.getLog());
            }
        }

        @Override
        public void dispose()
        {
            program.dispose();
        }

        @Override
        public void begin(Camera camera, RenderContext context)
        {
            this.camera = camera;
            this.ctx = context;
            program.begin();

            program.setUniformMatrix("u_projViewTrans", camera.combined);

            context.setDepthTest(GL_LEQUAL);
            context.setCullFace(GL_BACK);
        }

        @Override
        public void render(Renderable renderable)
        {
            program.setUniformMatrix("u_worldTrans", renderable.worldTransform);
            renderable.meshPart.render(program);
        }

        @Override
        public void end()
        {
            program.end();
        }

        @Override
        public int compareTo(Shader other)
        {
            return 0;
        }

        @Override
        public boolean canRender(Renderable instance)
        {
            return true;
        }

    };




    private Environment environment;
    private PerspectiveCamera camera;
    private CameraInputController cameraController;
    private ModelBatch modelBatch;
    private Model model;
    private ModelInstance instance;


    private Shader shaders;


    private AnimationController animation;

    ArrayList<String> animations;

    public static Texture BGTex;
    public static Sprite BGsprite;
    public SpriteBatch SpriteRenderer;


    @Override
    public void create() {

        animations = new ArrayList<String>();
        animations.addAll(Arrays.asList("Idle.g3dj", "Ninja_Idle_1.g3dj", "Situps.g3dj", "Hanging_Idle.g3dj", "Swing_To_Land.g3dj"));

        environment = new Environment();
        environment.set(
                new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f)
        );
        environment.add(
                new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f)
        );

        modelBatch = new ModelBatch();


        camera = new PerspectiveCamera(
                67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()
        );
        camera.position.set(10f, 150f, 150.0f);
        camera.lookAt(0, 150, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();


        int PseudoRandomIndex = (int)(Math.random() * animations.size());


        ModelBuilder modelBuilder = new ModelBuilder();
        model = new G3dModelLoader(
                new JsonReader()).loadModel(
                        Gdx.files.internal("Idle.g3dj")
        );
        instance = new ModelInstance(model);

        cameraController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(cameraController);

        animation = new AnimationController(instance);
        animation.animate("mixamo.com",  1, 1f, null, 0.2f);

        for (Animation a : instance.animations) {
           System.out.println(a.id);
        }

        // 2D setup
        SpriteRenderer = new SpriteBatch();

        Pixmap file = new Pixmap(Gdx.files.internal("CubeSchool.png"));
        Pixmap scaled = new Pixmap(
                (int)( file.getWidth() * (Gdx.graphics.getHeight()*1.0f/file.getHeight())),
                Gdx.graphics.getHeight()
                , file.getFormat());
        scaled.drawPixmap(file,
                0, 0, file.getWidth(), file.getHeight(),
                0, 0, scaled.getWidth(), scaled.getHeight()
        );
        Texture texture = new Texture(scaled);
        file.dispose();
        scaled.dispose();

        BGTex = texture;
        BGsprite = new Sprite(BGTex);

        // custom shaders
        //shaders = new MYshader();
        //shaders.init();

    }

    @Override
    public void render() {
        cameraController.update();



        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        float delta = Gdx.graphics.getDeltaTime();

        SpriteRenderer.begin();
        {
            BGsprite.draw(SpriteRenderer);
        }
        SpriteRenderer.end();

        modelBatch.begin(camera);
        {
            animation.update(delta);
            modelBatch.render(instance, environment);
        }
        modelBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }
}
